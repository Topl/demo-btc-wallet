package co.topl.btc.server

import org.http4s.ember.server.EmberServerBuilder
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.server.Router
import org.http4s.server.middleware._
import org.http4s.dsl.io._
import org.http4s.HttpRoutes
import org.http4s._
import org.http4s.server.staticcontent.resourceServiceBuilder
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.std.Console
import scala.concurrent.duration._
import cats.effect.IOApp
import cats.data.Kleisli
import org.typelevel.log4cats.slf4j.Slf4jLogger
import co.topl.btc.server.bitcoin.remoteConnection
import org.bitcoins.core.config.RegTest
import org.bitcoins.rpc.client.common.BitcoindRpcClient
import org.bitcoins.rpc.config.BitcoindAuthCredentials.PasswordBased
import scopt.OParser

import co.topl.btc.server.api.{apiService, BridgeWSClient}
import org.http4s.dsl.impl.Responses
import co.topl.btc.server.bitcoin.onStartup
import co.topl.btc.server.bitcoin.BitcoindExtended
import co.topl.btc.server.bitcoin.Services.mintBlock

object Main extends IOApp {
  def webUI() = HttpRoutes.of[IO] { case request @ GET -> Root =>
    StaticFile
      .fromResource("/static/index.html", Some(request))
      .getOrElseF(InternalServerError())
  }
  def router(bitcoind: BitcoindExtended, wsClient: BridgeWSClient) = 
    Router.define("/api" -> apiService(bitcoind, wsClient), "/" -> webUI())(default = resourceServiceBuilder[IO]("/static").toRoutes)

  def mintForever(bitcoind: BitcoindExtended, delay: Int): IO[Unit] = 
    mintBlock(bitcoind).andWait(delay.seconds).foreverM

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
      args.bitcoindUrl, 
      PasswordBased(args.bitcoindUser, args.bitcoindPassword)
    )
    val bridgeWsClient = BridgeWSClient(
      args.bridgeHost,
      args.bridgePort,
      EmberClientBuilder.default[IO].build
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
            router(bitcoindInstance, bridgeWsClient).run(request).getOrElse(Response.notFound)
          }
        )
        .withLogger(Slf4jLogger.getLogger[IO])
        .build
        .allocated
    } yield println(s"Server started on ${ServerConfig.host}:${ServerConfig.port}"))  &> mintForever(bitcoindInstance, args.mintTime)
    .as(ExitCode.Success)
    .handleErrorWith { e =>
      e.printStackTrace()
      Console[IO].errorln(s"Error caught: ${e.getMessage}").as(ExitCode.Error)
    } >> IO.never
  }

}