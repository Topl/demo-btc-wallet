package co.topl.btc.server

import io.circe.Json
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import org.typelevel.log4cats.slf4j.Slf4jLogger

package object api {
  val ApiService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}")))
  }
}
