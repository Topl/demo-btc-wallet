package co.topl.btc.server.bitcoin

import org.bitcoins.rpc.client.common.BitcoindRpcClient
import org.bitcoins.rpc.config.BitcoindInstance
import play.api.libs.json.{JsArray, JsString, JsValue, JsObject}
import cats.effect.IO
import org.bitcoins.crypto.DoubleSha256DigestBE
import org.bitcoins.core.protocol.BitcoinAddress
import org.bitcoins.core.currency.{Bitcoins, CurrencyUnit}
import play.api.libs.json.Json
import org.bitcoins.commons.serializers.JsonSerializers._
import org.bitcoins.commons.serializers.JsonWriters._
import org.bitcoins.commons.jsonmodels.bitcoind.ListTransactionsResult

import scala.concurrent.Future
import play.api.libs.json.JsNumber
import play.api.libs.json.JsBoolean

final class BitcoindExtended(impl: BitcoindInstance) extends BitcoindRpcClient(impl){
  import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO

  this.listTransactions()

  private def bitcoindCallRaw(
    command:         String,
    parameters:      List[JsValue] = List.empty,
    uriExtensionOpt: Option[String] = None
  ): Future[JsValue] = {
    val request =
      buildRequest(instance, command, JsArray(parameters), uriExtensionOpt)
    val responseF = sendRequest(request)

    val payloadF: Future[JsValue] =
      responseF.flatMap(getPayload(_))

    payloadF
  }

  def listWalletDirs(): IO[Seq[String]] = bitcoindCallRaw("listwalletdir")
    .map(res => (res \ "result" \\ "name").map(_.as[String]).toSeq)

  
  def sendToAddressWithFees(address: BitcoinAddress, amount: CurrencyUnit, wallet: String, feeRate: Int = 1): IO[DoubleSha256DigestBE] = 
    bitcoindCallRaw(
      "sendtoaddress",
      List(
        Json.toJson(address),
        Json.toJson(Bitcoins(amount.satoshis)),
        Json.toJson(None), // comment
        Json.toJson(None), // comment_to
        Json.toJson(None), // subtractfeefromamount
        Json.toJson(None), // replaceable
        Json.toJson(None), // conf_target
        Json.toJson(None), // estimate_mode  
        Json.toJson(None), // avoid_reuse
        JsNumber(feeRate)
      ),
      uriExtensionOpt = Some(walletExtension(wallet))
    )
    .map(res =>(res \ "result").as[String])
    .map(DoubleSha256DigestBE.fromHex(_))

  def listWalletTransactions(wallet: String): IO[Vector[ListTransactionsResult]] = 
    bitcoindCallRaw("listtransactions", uriExtensionOpt = Some(walletExtension(wallet)))
    .map(res =>(res \ "result").as[Vector[ListTransactionsResult]])
}

object BitcoindExtended {
  implicit def futureToIO[T](fut: Future[T]): IO[T] = IO.fromFuture(IO(fut))
  def apply(impl: BitcoindInstance): BitcoindExtended = new BitcoindExtended(impl)
}