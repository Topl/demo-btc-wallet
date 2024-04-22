package co.topl.btc.server

import akka.actor.ActorSystem
import org.bitcoins.core.config.NetworkParameters
import org.bitcoins.rpc.config.{BitcoindAuthCredentials, BitcoindInstanceLocal, BitcoindInstanceRemote}
import org.bitcoins.rpc.client.common.BitcoindRpcClient
import org.bitcoins.rpc.config.BitcoindInstance
import org.bitcoins.tor.Socks5ProxyParams
import cats.effect.IO
import co.topl.btc.server.bitcoin.BitcoindExtended
import co.topl.btc.server.bitcoin.Services.{initializeWallets, fundWallets}

import java.io.File
import java.net.URI
import co.topl.btc.server.bitcoin.Services.mintBlock

package object bitcoin {
    implicit val system: ActorSystem = ActorSystem("System")

      /**
     * Connection to the bitcoind RPC server instance
     * @param network Parameters of a given network to be used
     * @param host The host to connect to the bitcoind instance
     * @param credentials rpc credentials
     * @param binary the bitcoind executable
     * @return
     */
    def localConnection(
      network:     NetworkParameters,
      host:        String,
      credentials: BitcoindAuthCredentials,
      binary:      File
    ): BitcoindExtended = BitcoindExtended(
      BitcoindInstanceLocal(
        network = network,
        uri = new URI(s"$host:${network.port}"),
        rpcUri = new URI(s"$host:${network.rpcPort}"),
        authCredentials = credentials,
        binary = binary
      )
    )

    /**
     * Connection to the bitcoind RPC server instance
     *
     * @param network     Parameters of a given network to be used
     * @param host        The host to connect to the bitcoind instance
     * @param credentials rpc credentials
     * @param proxyParams proxy parameters
     * @return
     */
    def remoteConnection(
      network:     NetworkParameters,
      host:        String,
      credentials: BitcoindAuthCredentials,
      proxyParams: Option[Socks5ProxyParams] = None
    ): BitcoindExtended = BitcoindExtended(
      BitcoindInstanceRemote(
        network = network,
        uri = new URI(s"$host:${network.port}"),
        rpcUri = new URI(s"$host:${network.rpcPort}"),
        authCredentials = credentials,
        proxyParams = proxyParams
      )
    )

    def onStartup(bitcoind: BitcoindExtended): IO[Unit] = for {
      _ <- initializeWallets(bitcoind)
      _ <- fundWallets(bitcoind)
    } yield println("Bitcoind start-up complete.")
}
