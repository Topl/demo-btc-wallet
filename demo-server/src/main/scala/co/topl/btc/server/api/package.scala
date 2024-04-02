package co.topl.btc.server

import io.circe.Json
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.literal._
import org.http4s.HttpRoutes
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import cats.effect._
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import org.typelevel.log4cats.slf4j.Slf4jLogger

package object api {

  case class TransferRequest(address: String, quantity: Long)
  implicit val decoder: EntityDecoder[IO, TransferRequest] = jsonOf[IO, TransferRequest]

  val ApiService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case POST -> Root / "wallets" => Ok("Wallet 123 Created")
    case GET -> Root / "wallets" => Ok(Seq("default", "dummy-wallet", "MyWallet").asJson)
    case r @ POST -> Root / "wallets" / walletName / "transfer" => for {
      req <- r.as[TransferRequest]
      resp <- Ok(s"Sent ${req.quantity} Satoshis from $walletName to ${req.address}")
    } yield resp
  }
}
