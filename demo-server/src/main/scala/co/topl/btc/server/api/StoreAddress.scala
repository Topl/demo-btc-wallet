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
import co.topl.btc.server.persistence.StateApi
import io.circe.Json


object StoreAddress {
  /**
    * A case class representing a Store Address request
    */
  case class StoreAddressRequest(address: String, idx: Int)
  
  implicit val storeAddressRequestDecoder: EntityDecoder[IO, StoreAddressRequest] =
    jsonOf[IO, StoreAddressRequest]
  
  /**
    * An HTTP handler for the store address endpoint
    *
    * @param r The request to handle
    * @param bitcoind The bitcoind instance to use
    * @return An IO monad containing the response
    */
  def handler(r: Request[IO], bitcoind: BitcoindExtended, stateApi: StateApi): IO[Response[IO]] = for {
    req <- r.as[StoreAddressRequest]
    _ <- stateApi.storeEscrowAddress(req.address, req.idx)
    resp <- Ok()
  } yield resp

}
