package co.topl.btc.server

import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware._
import org.http4s.dsl.io._
import org.http4s.HttpRoutes
import org.http4s._
import org.http4s.server.staticcontent.resourceServiceBuilder
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.data.Kleisli
import org.typelevel.log4cats.slf4j.Slf4jLogger

import co.topl.btc.server.api.ApiService
import org.http4s.dsl.impl.Responses

object Main extends IOApp {
  def webUI() = HttpRoutes.of[IO] { case request @ GET -> Root =>
    StaticFile
      .fromResource("/static/index.html", Some(request))
      .getOrElseF(InternalServerError())
  }
  val router = 
    Router.define("/api" -> ApiService, "/" -> webUI())(default = resourceServiceBuilder[IO]("/static").toRoutes)
  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withIdleTimeout(ServerConfig.idleTimeOut)
      .withHost(ServerConfig.host)
      .withPort(ServerConfig.port)
      .withHttpApp(
        Kleisli[IO, Request[IO], Response[IO]] { request =>
          router.run(request).getOrElse(Response.notFound)
        }
      )
      .withLogger(Slf4jLogger.getLogger[IO])
      .build
      .allocated
      .handleErrorWith { e =>
        e.printStackTrace()
        IO(e.getMessage)
      } >> (IO.println(s"Server started on ${ServerConfig.host}:${ServerConfig.port}") *> IO.never)

}