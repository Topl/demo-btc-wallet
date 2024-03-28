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

import co.topl.btc.server.api._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val logger = Slf4jLogger.getLogger[IO]
    EmberServerBuilder
      .default[IO]
      .withIdleTimeout(ServerConfig.idleTimeOut)
      .withHost(ServerConfig.host)
      .withPort(ServerConfig.port)
      .withHttpApp(Router("/api" -> ApiService).orNotFound)
      .withLogger(logger)
      .build
      .allocated
      .handleErrorWith { e =>
        e.printStackTrace()
        IO {
          Left(e.getMessage)
        }
      } >> (IO.println(s"Server started on ${ServerConfig.host}:${ServerConfig.port}") *> IO.never)
  }

}