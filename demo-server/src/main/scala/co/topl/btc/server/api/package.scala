package co.topl.btc.server

import cats.effect.IO
import co.topl.btc.server.bitcoin.BitcoindExtended
import cats.effect.kernel.Resource
import org.http4s.client.Client
import org.http4s.implicits._
import io.circe.generic.auto._
import io.circe.Json
import io.circe.syntax._
import org.http4s._
import org.http4s.Uri._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.EntityEncoder


package object api {
  case class BridgeWSClient(client: Resource[IO, Client[IO]])
  case class ConfirmDepositRequest(sessionID: String, amount: Long)

  implicit val reqDecoder: EntityDecoder[IO, ConfirmDepositRequest] = jsonOf[IO, ConfirmDepositRequest]

  import cats.effect.unsafe.implicits.global

  // Define the API service routes
  def apiService(bitcoind: BitcoindExtended, wsClient: BridgeWSClient): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case r @ POST -> Root / "transfer" => TransferRequest.handler(r, bitcoind)
    case GET -> Root / "getBalances" / walletName => FetchBalances.handler(walletName, bitcoind)
    case GET -> Root / "listTransactions" / walletName => ListTransactions.handler(walletName, bitcoind)
  }
}
