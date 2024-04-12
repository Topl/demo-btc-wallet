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
  case class BridgeWSClient(wsUrl: String, client: Resource[IO, Client[IO]])
  case class ConfirmDepositRequest(sessionID: String, amount: Long)
  case class ConfirmDepositResponse(
    txId: String,
    redeemAddress: String
)

  implicit val reqDecoder: EntityDecoder[IO, ConfirmDepositRequest] = jsonOf[IO, ConfirmDepositRequest]
  implicit val resDecoder: EntityDecoder[IO, ConfirmDepositResponse] = jsonOf[IO, ConfirmDepositResponse]

  import cats.effect.unsafe.implicits.global

  def comfirmPegInDeposit(ws: BridgeWSClient): ConfirmDepositRequest => IO[Unit] = (depositReq: ConfirmDepositRequest) =>
{    ws.client.use(client => client.status({
  println("here")
      println(depositReq.asJson)
      val x = Request[IO](method = Method.POST, uri = Uri(Some(Scheme.http), Some(Authority(port= Some(4000))), Root / "api"/ "confirm-deposit-btc")).withEntity(depositReq.asJson)
      println(x)
      x
    }
    ).flatMap(IO.println(_))
  )}

  // Define the API service routes
  def apiService(bitcoind: BitcoindExtended, wsClient: BridgeWSClient): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case r @ POST -> Root / "transfer" => {
      println("why")
      TransferRequest.handler(r, bitcoind, comfirmPegInDeposit(wsClient))
    }
  }
}
