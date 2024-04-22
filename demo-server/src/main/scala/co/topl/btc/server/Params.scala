package co.topl.btc.server

import scopt.OParser

final case class Params(
  bitcoindUrl: String = "http://localhost", 
  bitcoindUser: String = "", 
  bitcoindPassword: String = "",
  bridgeUrl: String = "http://localhost",
  bridgePort: Int = 4000
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
      opt[String]("bridge-url")
        .action((x, c) => c.copy(bitcoindUrl = x))
        .text(
          "The URL to connect to a bridge WS instance. (default: http://localhost)"
        ),
      opt[String]("bridge-port")
        .action((x, c) => c.copy(bitcoindUrl = x))
        .text(
          "The port to connect to a bridge WS instance. (default: 4000)"
        ),
    )
  }

  def parseParams(args: List[String]): Option[Params] = OParser.parse(parser, args, Params())
}
