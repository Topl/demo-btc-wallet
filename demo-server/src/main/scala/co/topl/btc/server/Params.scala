package co.topl.btc.server

import scopt.OParser

final case class Params(
  bitcoindUrl: String = "http://localhost", 
  bitcoindUser: String = "", 
  bitcoindPassword: String = "",
  bridgeHost: String = "localhost",
  bridgePort: Int = 4000,
  mintTime: Int = 90 // in seconds
)

object Params {
  private val parser = {
    val builder = OParser.builder[Params]
    import builder._
    OParser.sequence(
      programName("demo-btc-wallet"),
      opt[String]("btc-url")
        .action((x, c) => c.copy(bitcoindUrl = x))
        .text(
          "The URL to connect to a bitcoind instance. (default: http://localhost)"
        ),
      opt[String]("btc-user")
        .required()
        .action((x, c) => c.copy(bitcoindUser = x))
        .text(
          "The username to connect to a bitcoind instance. (required)"
        ),
      opt[String]("btc-password")
        .required()
        .action((x, c) => c.copy(bitcoindPassword = x))
        .text(
          "The password to connect to a bitcoind instance. (required)"
        ),
      opt[String]("bridge-host")
        .action((x, c) => c.copy(bridgeHost = x))
        .text(
          "The host to connect to a bridge WS instance. (default: localhost)"
        ),
      opt[Int]("bridge-port")
        .action((x, c) => c.copy(bridgePort = x))
        .text(
          "The port to connect to a bridge WS instance. (default: 4000)"
        ),
      opt[Int]("mint-time")
        .action((x, c) => c.copy(mintTime = x))
        .text(
          "Regtest mode only. The time (in seconds) between block minting. (default: 90 seconds)"
        )
        .validate(x =>
          if (x >= 1 && x <= 3600) success // 1 hour
          else failure("Mint interval must be between 1 second and 3600 seconds (1 hour)")
        ),
    )
  }

  def parseParams(args: List[String]): Option[Params] = OParser.parse(parser, args, Params())
}
