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
  case class BridgeWSClient(wsHost: String, wsPort: Int, client: Resource[IO, Client[IO]])
  case class ConfirmDepositRequest(sessionID: String, amount: Long)

  implicit val reqDecoder: EntityDecoder[IO, ConfirmDepositRequest] = jsonOf[IO, ConfirmDepositRequest]

  import cats.effect.unsafe.implicits.global

  def comfirmPegInDeposit(ws: BridgeWSClient): ConfirmDepositRequest => IO[Unit] = (depositReq: ConfirmDepositRequest) =>
    ws.client.use(client => 
      client.status(
        Request[IO](method = Method.POST, uri = Uri(Some(Scheme.http), Some(Authority(host = RegName(ws.wsHost), port= Some(ws.wsPort))), Root / "api"/ "confirm-deposit-btc")).withEntity(depositReq.asJson)
      ).flatMap(status => IO.println(s"confirm deposit status: $status")))

  // Define the API service routes
  def apiService(bitcoind: BitcoindExtended, wsClient: BridgeWSClient): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case r @ POST -> Root / "transfer" => TransferRequest.handler(r, bitcoind, comfirmPegInDeposit(wsClient))
  }
}
