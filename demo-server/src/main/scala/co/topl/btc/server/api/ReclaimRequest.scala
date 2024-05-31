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
    def handler(r: Request[IO], bitcoind: BitcoindExtended, stateApi: StateApi): IO[Response[IO]] = for {
      req <- r.as[ReclaimRequest]
      
      inputData <- getTxOutForAddress(bitcoind, req.toWallet, BitcoinAddress(req.fromAddress))
      idxOpt <- stateApi.getIndexForAddress(req.fromAddress)

      resp <- (inputData, idxOpt) match {
        case (Some((txOut, amount)), Some(idx)) => for {
          toAddress <- futureToIO(bitcoind.getNewAddress(walletNameOpt= Some(req.toWallet)))
        } yield ???
        case _ => IO.pure(BadRequest("Address not found"))
      }

      // TODO: display script hex on the bridge UI
      // store this script in this demo wallet
      // prove tx (construct signature, serialize tx, etc)
      // broadcast

      // to test mint 1000 blocks

      // write tests
      // txId <- bitcoind.sendToAddressWithFees(req.toAddress, req.quantity, req.fromWallet)
    } yield resp

}
