package co.topl.btc.server.bitcoin

import org.bitcoins.rpc.client.common.BitcoindRpcClient
import org.bitcoins.rpc.config.BitcoindInstance
import play.api.libs.json.{JsArray, JsString, JsValue}
import cats.effect.IO
import org.bitcoins.crypto.DoubleSha256DigestBE

import scala.concurrent.Future

final class BitcoindExtended(impl: BitcoindInstance) extends BitcoindRpcClient(impl){
  import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO

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
  implicit def futureToIO[T](fut: Future[T]): IO[T] = IO.fromFuture(IO(fut))
  def apply(impl: BitcoindInstance): BitcoindExtended = new BitcoindExtended(impl)
}