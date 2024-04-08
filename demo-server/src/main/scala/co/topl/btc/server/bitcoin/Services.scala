package co.topl.btc.server.bitcoin

import cats.effect.IO
import org.bitcoins.rpc.client.common.BitcoindRpcClient
import Constants._

object Services {
  // Create or load wallets
  def initializeWallets(bitcoind: BitcoindRpcClient): IO[Unit] = ???

  // funds from minting wallet.. if minting wallet does not have enough, mint more
  def fundWallet(bitcoind: BitcoindRpcClient): IO[Unit] = ???
}
