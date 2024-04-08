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
import co.topl.btc.server.bitcoin.remoteConnection
import org.bitcoins.core.config.RegTest
import org.bitcoins.rpc.client.common.BitcoindRpcClient
import org.bitcoins.rpc.config.BitcoindAuthCredentials.PasswordBased
import scopt.OParser

import co.topl.btc.server.api.ApiService
import org.http4s.dsl.impl.Responses
import co.topl.btc.server.bitcoin.onStartup

object Main extends IOApp {
  def webUI() = HttpRoutes.of[IO] { case request @ GET -> Root =>
    StaticFile
      .fromResource("/static/index.html", Some(request))
      .getOrElseF(InternalServerError())
  }
  val router = 
    Router.define("/api" -> ApiService, "/" -> webUI())(default = resourceServiceBuilder[IO]("/static").toRoutes)

  override def run(args: List[String]): IO[ExitCode] = Params.parseParams(args) match {
    case Some(config) =>
      runWithArgs(config)
    case None =>
      IO.consoleForIO.errorln("Invalid arguments") *>
        IO(ExitCode.Error)
  }

  def runWithArgs(args: Params): IO[ExitCode] = {
    val bitcoindInstance = remoteConnection(
      RegTest, 
      args.bitcoindHost, 
      PasswordBased(args.bitcoindUser, args.bitcoindPassword)
    )
    (for {
      _ <- onStartup(bitcoindInstance)
      _ <- EmberServerBuilder
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
    } yield s"Server started on ${ServerConfig.host}:${ServerConfig.port}")
    .handleErrorWith { e =>
      e.printStackTrace()
      IO(e.getMessage)
    } >> IO.never
  }

}