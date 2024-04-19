package co.topl.btc.server.api

import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import cats.effect.IO
import io.circe.{ Decoder, Encoder, HCursor, Json }

import org.bitcoins.core.protocol.BitcoinAddress
import org.bitcoins.core.currency.Satoshis
import co.topl.btc.server.bitcoin.BitcoindExtended
import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO
import io.circe.Json


object FetchBalances {
  /**
    * A case class representing a fetch balances response
    */
  case class FetchBalancesResponse(trusted: Long, untrustedPending: Long, immature: Long)
  /**
    * An HTTP handler for the fetch balance endpoint
    *
    * @param r The request to handle
    * @param bitcoind The bitcoind instance to use
    * @return An IO monad containing the response
    */
  def handler(wallet: String, bitcoind: BitcoindExtended): IO[Response[IO]] = for {
    balances <- futureToIO(bitcoind.getBalances(wallet))
    resp <- Ok(FetchBalancesResponse(
      0, 
      balances.mine.untrusted_pending.satoshis.toLong, 
      balances.mine.immature.satoshis.toLong
    ).asJson)
  } yield resp

}
