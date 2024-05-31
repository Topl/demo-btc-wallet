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
import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO
import io.circe.Json
import co.topl.btc.server.bitcoin.KeyGenerationUtils
import co.topl.btc.server.persistence.StateApi
import org.bitcoins.core.number.UInt32


object GetPublicKey {
  /**
    * A case class representing a get public key response
    */
  case class GetPublicKeyResponse(pubKey: String, idx: Int)
  /**
    * An HTTP handler for the get public key endpoint
    *
    * @param r The request to handle
    * @param bitcoind The bitcoind instance to use
    * @return An IO monad containing the public key, and the index used to derive it
    */
  def handler(wallet: String, bitcoind: BitcoindExtended, stateApi: StateApi): IO[Response[IO]] = for {
    mainPrivKey <- KeyGenerationUtils.loadMainKey(wallet, bitcoind)
    nextIdx <- {
      println(mainPrivKey)
      stateApi.getNextIndex()
    }
    childPubKey = {
      println(nextIdx)
      mainPrivKey.deriveChildPrivKey(UInt32(nextIdx)).publicKey
    }
    resp <- Ok(GetPublicKeyResponse(childPubKey.hex, nextIdx.toInt).asJson)
  } yield resp

}
