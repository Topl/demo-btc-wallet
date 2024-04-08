package co.topl.btc.server.bitcoin

import org.bitcoins.rpc.client.common.BitcoindRpcClient
import org.bitcoins.rpc.config.BitcoindInstance
import play.api.libs.json.{JsArray, JsString, JsValue}
import cats.effect.IO

import scala.concurrent.Future

final class BitcoindExtended(impl: BitcoindInstance) extends BitcoindRpcClient(impl){

  implicit def futureToIO[T](fut: Future[T]): IO[T] = IO.fromFuture(IO(fut))

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
}

object BitcoindExtended {
  def apply(impl: BitcoindInstance): BitcoindExtended = new BitcoindExtended(impl)
}