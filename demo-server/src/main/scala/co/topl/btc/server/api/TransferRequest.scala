package co.topl.btc.server.api

import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import cats.effect.IO

/**
  * A case class representing a BTC transfer request
  */
case class TransferRequest(fromWallet: String, toAddress: String, quantity: String)


object TransferRequest {

    /**
      * A Circe JSON decoder for the transfer request
      */
    implicit val decoder: EntityDecoder[IO, TransferRequest] = jsonOf[IO, TransferRequest]

    /**
      * An HTTP handler for the transfer request
      *
      * @param r The request to handle
      * @return An IO monad containing the response
      */
    def handler(r: Request[IO]): IO[Response[IO]] = for {
      req <- r.as[TransferRequest]
      resp <- Ok(s"Sent ${req.quantity} Satoshis from ${req.fromWallet} to ${req.toAddress}")
    } yield resp

}
