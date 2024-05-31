package co.topl.btc.server.api

import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import cats.effect.IO

import co.topl.btc.server.bitcoin.BitcoindExtended
import co.topl.btc.server.bitcoin.KeyGenerationUtils
import co.topl.btc.server.persistence.StateApi


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
    nextIdx <- stateApi.getNextIndex()
    childPubKey = KeyGenerationUtils.generatePublicKey(mainPrivKey, nextIdx)
    resp <- Ok(GetPublicKeyResponse(childPubKey.hex, nextIdx.toInt).asJson)
  } yield resp

}
