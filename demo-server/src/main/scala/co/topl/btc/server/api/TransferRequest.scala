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

/**
  * A case class representing a BTC transfer request
  */
case class TransferRequest(fromWallet: String, toAddress: BitcoinAddress, quantity: Satoshis)


object TransferRequest {
    /**
      * A Circe JSON decoders for the transfer request
      */
    implicit val decodeTransferRequest: Decoder[TransferRequest] = new Decoder[TransferRequest] {
      final def apply(c: HCursor): Decoder.Result[TransferRequest] =
        for {
          fromWallet <- c.downField("fromWallet").as[String]
          toAddress <- c.downField("toAddress").as[String]
          quantity <- c.downField("quantity").as[Int]
        } yield TransferRequest(fromWallet, BitcoinAddress(toAddress), Satoshis(quantity))
    }
    implicit val decoder: EntityDecoder[IO, TransferRequest] = jsonOf[IO, TransferRequest]

    /**
      * An HTTP handler for the transfer request
      *
      * @param r The request to handle
      * @param bitcoind The bitcoind instance to use
      * @return An IO monad containing the response
      */
    def handler(r: Request[IO], bitcoind: BitcoindExtended): IO[Response[IO]] = for {
      req <- r.as[TransferRequest]
      txId <- bitcoind.sendToAddressWithFees(req.toAddress, req.quantity, req.fromWallet)
      resp <- Ok(txId.hex)
    } yield resp

}
