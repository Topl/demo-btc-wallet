package co.topl.btc.server.api

import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import cats.effect.IO
import io.circe.{ Decoder, Encoder, HCursor, Json }

import org.bitcoins.core.protocol.BitcoinAddress
import org.bitcoins.core.currency.Satoshis
import co.topl.btc.server.bitcoin.BitcoindExtended
import co.topl.btc.server.bitcoin.Services.mintBlock
import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO
import io.circe.Json
import co.topl.btc.server.persistence.StateApi
import co.topl.btc.server.bitcoin.Services.getTxOutForAddress
import co.topl.btc.server.bitcoin.Builders._
import co.topl.btc.server.bitcoin.KeyGenerationUtils
import org.bitcoins.core.protocol.script.RawScriptPubKey

object ReclaimRequest {

  /**
  * A case class representing a BTC reclaim request
  */
  case class ReclaimRequest(toWallet: String, fromAddress: String)

  implicit val storeAddressRequestDecoder: EntityDecoder[IO, ReclaimRequest] =jsonOf[IO, ReclaimRequest]


    /**
      * An HTTP handler for the transfer request
      *
      * @param r The request to handle
      * @param bitcoind The bitcoind instance to use
      * @return An IO monad containing the response
      */
    def handler(r: Request[IO], bitcoind: BitcoindExtended, stateApi: StateApi): IO[Response[IO]] = (for {
      req <- r.as[ReclaimRequest]
      
      inputData <- getTxOutForAddress(bitcoind, req.toWallet, BitcoinAddress(req.fromAddress))
      infoOpt <- stateApi.getInfoForAddress(req.fromAddress)

      resp <- (inputData, infoOpt) match {
        case (Some((txOut, amount, numConf)), Some((idx, script))) => 
          if(numConf < 1000) IO.pure(BadRequest(s"Too early to reclaim. 1000 blocks have not yet elapsed. Number of confirmations: $numConf"))
          else for {
            toAddress <- futureToIO(bitcoind.getNewAddress(walletNameOpt= Some(req.toWallet)))
            unprovenTx <- buildUnprovenReclaimTx(bitcoind, txOut, toAddress, amount)
            mainKey <- KeyGenerationUtils.loadMainKey(req.toWallet, bitcoind)
            provenTx = proveReclaimTx(unprovenTx, mainKey, RawScriptPubKey.fromAsmHex(script), idx, amount)
            txId <- futureToIO(bitcoind.sendRawTransaction(provenTx))
          } yield Ok(txId.hex)
        case _ => IO.pure(BadRequest("Address not found"))
      }
    } yield resp).flatten

}
