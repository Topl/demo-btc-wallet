package co.topl.btc.server

import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import cats.effect.IO
import co.topl.btc.server.bitcoin.BitcoindExtended

package object api {

  // Define the API service routes
  def apiService(bitcoind: BitcoindExtended): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case r @ POST -> Root / "transfer" => TransferRequest.handler(r, bitcoind)
  }
}
