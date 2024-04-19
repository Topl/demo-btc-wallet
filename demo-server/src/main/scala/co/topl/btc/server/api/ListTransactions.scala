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


object ListTransactions {
  /**
    * A case class representing a list transactions response
    */
  case class ListTransactionsResponse(address: String, category: String, amount: Long, time: Long)
  /**
    * An HTTP handler for the list transactions endpoint
    *
    * @param r The request to handle
    * @param bitcoind The bitcoind instance to use
    * @return An IO monad containing the response
    */
  def handler(wallet: String, bitcoind: BitcoindExtended): IO[Response[IO]] = for {
    txs <- bitcoind.listWalletTransactions(wallet)
    resp <- Ok(txs.map(tx => ListTransactionsResponse(tx.address.get.toString(), tx.category, tx.amount.toBigDecimal.longValue, tx.time.toLong)).asJson)
  } yield resp

}
