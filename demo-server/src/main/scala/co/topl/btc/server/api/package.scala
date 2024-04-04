package co.topl.btc.server

import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import cats.effect.IO

package object api {

  // Define the API service routes
  val ApiService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case r @ POST -> Root / "transfer" => TransferRequest.handler(r)
  }
}
