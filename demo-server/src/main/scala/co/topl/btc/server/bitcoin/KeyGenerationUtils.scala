package co.topl.btc.server.bitcoin

import cats.effect.IO
import org.bitcoins.core.hd.BIP32Path
import scodec.bits.ByteVector
import org.bitcoins.crypto.ECDigitalSignature
import org.bitcoins.crypto.ECPublicKey
import org.bitcoins.core.crypto.ExtPrivateKey

import org.bitcoins.core.number.UInt32

object KeyGenerationUtils {  
    /**
     * Derive a key from the specified wallet that can be used as our "main" key. 
     * We will further derive this key using some index to use in transactions.
    */
  def loadMainKey(
     wallet: String, 
     bitcoind: BitcoindExtended
  ): IO[ExtPrivateKey] = for {
    walletPrivateDescriptors <- bitcoind.listDescriptors(wallet, isPrivate = true)
  } yield {
    val mRaw = (walletPrivateDescriptors.head \ "desc").result.get.toString()
    val m = ExtPrivateKey.fromString(mRaw.substring(mRaw.indexOf("(") + 1, mRaw.indexOf("/")))
    // m / purpose' / coin_type' / account' / change / index ... BIP-044
    // our existing indices do not follow this scheme, so we need to choose a purpose that is not already in use
    // Per Bip-43 (and other Bips), purposes known to be in use are (non-exhaustive): 0, 44, 49, 86, 84, (1852 for cardano ed25519)
    // For now, we will choose 7091 (which is our coin_type, but not recognized since our purpose is not 44)
    val mainKeyPath = BIP32Path.fromString("m/7091'/1'/0'/0")
    m.deriveChildPrivKey(mainKeyPath)
  }

  def signWithMainKey(
      mainKey: ExtPrivateKey,
      txBytes: ByteVector,
      currentIdx: Int
  ): ECDigitalSignature = mainKey.deriveChildPrivKey(UInt32(currentIdx)).sign(txBytes)

  def generatePublicKey(
      mainKey: ExtPrivateKey,
      currentIdx: Int
  ): ECPublicKey = mainKey.deriveChildPrivKey(UInt32(currentIdx)).publicKey
}
