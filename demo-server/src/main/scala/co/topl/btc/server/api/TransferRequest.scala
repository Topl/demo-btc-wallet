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
import co.topl.btc.server.bitcoin.Services.MintingWallet
import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO
import TransferRequest.PegInOpts
import io.circe.Json

/**
  * A case class representing a BTC transfer request
  */
case class TransferRequest(fromWallet: String, toAddress: BitcoinAddress, quantity: Satoshis, pegInOpts: Option[PegInOpts] = None)


object TransferRequest {
  case class PegInOpts(sessionID: String)

    /**
      * A Circe JSON decoders for the transfer request
      */
    implicit val decodeTransferRequest: Decoder[TransferRequest] = new Decoder[TransferRequest] {
      final def apply(c: HCursor): Decoder.Result[TransferRequest] =
        for {
          fromWallet <- c.downField("fromWallet").as[String]
          toAddress <- c.downField("toAddress").as[String]
          quantity <- c.downField("quantity").as[Int]
          transferType <- c.downField("transferType").as[String]
          pegInOpts <- transferType match {
            //Look how you did it for the vault 
            case "peginDeposit" => c.downField("pegInOptions").downField("sessionId").as[String].map(sId => Some(PegInOpts(sId)))
            case _ => Right(None)
          }
        } yield TransferRequest(fromWallet, BitcoinAddress(toAddress), Satoshis(quantity), pegInOpts)
    }
    implicit val decoder: EntityDecoder[IO, TransferRequest] = jsonOf[IO, TransferRequest]

    /**
      * An HTTP handler for the transfer request
      *
      * @param r The request to handle
      * @param bitcoind The bitcoind instance to use
      * @return An IO monad containing the response
      */
    def handler(r: Request[IO], bitcoind: BitcoindExtended, notifyPegInDeposit: ConfirmDepositRequest => IO[Unit]): IO[Response[IO]] = for {
      req <- r.as[TransferRequest]
      txId <- bitcoind.sendToAddressWithFees(req.toAddress, req.quantity, req.fromWallet)
      // Manually mint a new block.. will be removed in the future
      mintAddr <- futureToIO(bitcoind.getNewAddress(walletNameOpt = Some(MintingWallet)))
      _ <- futureToIO(bitcoind.generateToAddress(1, mintAddr))
      _ <- req.pegInOpts match {
        case Some(opts) => notifyPegInDeposit(ConfirmDepositRequest(opts.sessionID, req.quantity.satoshis.toLong)).start.void
        case None => IO.unit
      }
      resp <- Ok(txId.hex)
    } yield resp

}
