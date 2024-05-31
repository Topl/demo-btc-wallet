package co.topl.btc.server.bitcoin

import akka.actor.ActorSystem
import cats.arrow.FunctionK
import cats.effect.IO
import org.bitcoins.commons.jsonmodels.bitcoind.BalanceInfo
import org.bitcoins.core.currency.{Bitcoins, BitcoinsInt, CurrencyUnit}
import org.bitcoins.core.number.{Int32, UInt32}
import org.bitcoins.core.protocol.script.{RawScriptPubKey, ScriptSignature}
import org.bitcoins.core.protocol.transaction._
import org.bitcoins.core.protocol.{BitcoinAddress, CompactSizeUInt}
import org.bitcoins.core.script.constant.ScriptToken
import org.bitcoins.core.util.BytesUtil
import org.bitcoins.crypto._
import play.api.libs.json._
import scodec.bits.ByteVector

import java.security.MessageDigest
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Random, Success, Try}
import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO

object Builders {
  def buildUnprovenReclaimTx(
    bitcoind: BitcoindExtended, 
    fromTxOut: TransactionOutPoint,
    toAddr: BitcoinAddress, 
    fromAmount: Bitcoins
  ): IO[Transaction] = {
    // Reclaim tx is spending from a time lock: 1000 blocks in the future
    val sequence: UInt32 = UInt32(1000L & TransactionConstants.sequenceLockTimeMask.toLong)
    val input = TransactionInput(fromTxOut, ScriptSignature.empty, sequence)
    val outputs = Map(toAddr -> fromAmount)
    for {
      tx <- futureToIO(bitcoind.createRawTransaction(Vector(input), outputs))
      feeRate <- futureToIO(bitcoind.getFeeRate())
      fee = feeRate.calc(tx)
      outputsWithFee = Map(toAddr -> Bitcoins((fromAmount - fee).satoshis))
      txWithFee <- futureToIO(bitcoind.createRawTransaction(Vector(input), outputsWithFee))
    } yield txWithFee
  }

  def getTxSignature(unsignedTx: Transaction, script: RawScriptPubKey, privateKey: String, inputAmount: CurrencyUnit): ECDigitalSignature = {
    val serializedTxForSignature = serializeForSignature(unsignedTx, inputAmount, script.asm)
    val signableBytes = CryptoUtil.doubleSHA256(serializedTxForSignature)
    val signature = ECPrivateKey.fromHex(privateKey).sign(signableBytes.bytes)
    // append 1 byte hash type onto the end, per BIP-066
    ECDigitalSignature(signature.bytes ++ ByteVector.fromByte(HashType.sigHashAll.byte))
  }

    /**
   * BIP-143
   * Double SHA256 of the serialization of:
   * 1. nVersion of the transaction (4-byte little endian)
   * 2. hashPrevouts (32-byte hash)
   * 3. hashSequence (32-byte hash)
   * 4. outpoint (32-byte hash + 4-byte little endian)
   * 5. scriptCode of the input (serialized as scripts inside CTxOuts)
   * 6. value of the output spent by this input (8-byte little endian)
   * 7. nSequence of the input (4-byte little endian)
   * 8. hashOutputs (32-byte hash)
   * 9. nLocktime of the transaction (4-byte little endian)
   * 10. sighash type of the signature (4-byte little endian)
   *
   * We are assuming hashtype is SIGHASH_ALL and sigVersion is SIGVERSION_WITNESS_V0
   *
   * The following was reverse engineered from the bitcoin core implementation
   */
  def serializeForSignature(
    txTo:        Transaction,
    inputAmount: CurrencyUnit, // amount in the output of the previous transaction (what we are spending)
    inputScript: Seq[ScriptToken]
  ): ByteVector = {
    val hashPrevouts: ByteVector = {
      val prevOuts = txTo.inputs.map(_.previousOutput)
      val bytes: ByteVector = BytesUtil.toByteVector(prevOuts)
      CryptoUtil.doubleSHA256(bytes).bytes // result is in little endian
    }

    val hashSequence: ByteVector = {
      val sequences = txTo.inputs.map(_.sequence)
      val littleEndianSeq =
        sequences.foldLeft(ByteVector.empty)(_ ++ _.bytes.reverse)
      CryptoUtil.doubleSHA256(littleEndianSeq).bytes // result is in little endian
    }

    val hashOutputs: ByteVector = {
      val outputs = txTo.outputs
      val bytes = BytesUtil.toByteVector(outputs)
      CryptoUtil.doubleSHA256(bytes).bytes // result is in little endian
    }

    val scriptBytes = BytesUtil.toByteVector(inputScript)

    val i = txTo.inputs.head
    val serializationForSig: ByteVector =
      txTo.version.bytes.reverse ++ hashPrevouts ++ hashSequence ++
      i.previousOutput.bytes ++ CompactSizeUInt.calc(scriptBytes).bytes ++
      scriptBytes ++ inputAmount.bytes ++ i.sequence.bytes.reverse ++
      hashOutputs ++ txTo.lockTime.bytes.reverse ++ Int32(HashType.sigHashAll.num).bytes.reverse
    serializationForSig
  }
}
