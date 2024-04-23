package co.topl.btc.server.bitcoin

import cats.effect.IO
import cats.implicits._
import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO
import org.bitcoins.core.currency.Bitcoins

object Services {
  val MintingWallet = "minting"
  val DefaultWallet = "default"
  val minFunds = 10 // Minimum funds we want our default wallet to have
  val InitialFunds = 100 // To default wallet
  val InitialBlocksToGenerate = 150 // 150 should initialize the minting wallet with 2,500 BTC (arbitrary large number to avoid running out of funds)
  val InitialFundsToMint = (InitialBlocksToGenerate - 100) * 50 // To minting wallet, 2,500 BTC

  // Create or load wallets
  def initializeWallets(bitcoind: BitcoindExtended): IO[Unit] = for {
    allWallets <- bitcoind.listWalletDirs()
    _ <- if(allWallets.contains(MintingWallet)) IO.unit else futureToIO(bitcoind.createWallet(MintingWallet))
    _ <- if(allWallets.contains(DefaultWallet)) IO.unit else futureToIO(bitcoind.createWallet(DefaultWallet))
    loadedWallets <- futureToIO(bitcoind.listWallets)
    unloadedWallets = allWallets.filterNot(loadedWallets.contains)
    res <- unloadedWallets.map(bitcoind.loadWallet).map(futureToIO).sequence
  } yield println("All wallets are loaded: " + (allWallets.toSet + MintingWallet + DefaultWallet).mkString(", "))

  // Only valid for RegTest
  private def fundMintingWallet(bitcoind: BitcoindExtended): IO[Unit] = for {
    currentBalance <- futureToIO(bitcoind.getBalance(MintingWallet))
    needsFunding = currentBalance.toBigDecimal <= BigDecimal(InitialFunds)
    newBalance <- if(needsFunding) for {
      _ <- mintBlock(bitcoind, InitialBlocksToGenerate)
      balRes <- futureToIO(bitcoind.getBalance(MintingWallet)).iterateUntil(_.toBigDecimal >= BigDecimal(InitialFundsToMint))
    } yield balRes
     else IO.pure(currentBalance)
  } yield println("Minting wallet funded: " + newBalance)

  private def fundDefaultWallet(bitcoind: BitcoindExtended): IO[Unit] = for {
    currentBalance <- futureToIO(bitcoind.getBalance(DefaultWallet))
    needsFunding = currentBalance.toBigDecimal <= BigDecimal(minFunds)
    newBalance <- if(needsFunding) for {
      addr <- futureToIO(bitcoind.getNewAddress(walletNameOpt = Some(DefaultWallet)))
      // If Default wallet does not have enough, transfer funds from minting wallet
      _ <- bitcoind.sendToAddressWithFees(addr, Bitcoins(InitialFunds), MintingWallet)
      _ <- mintBlock(bitcoind)
      balRes <- futureToIO(bitcoind.getBalance(DefaultWallet)).iterateUntil(_.toBigDecimal >= BigDecimal(InitialFunds))
    } yield balRes
     else IO.pure(currentBalance)
  } yield println("Default wallet funded: " + newBalance)

  // Fund default wallet. Preconditions: Both wallets are loaded
  def fundWallets(bitcoind: BitcoindExtended): IO[Unit] = for {
    _ <- fundMintingWallet(bitcoind)
    _ <- fundDefaultWallet(bitcoind)
  } yield println("Both Minting and Default wallets are funded")

  def mintBlock(bitcoind: BitcoindExtended, numBlocks: Int = 1): IO[Unit] = for {
    addr <- futureToIO(bitcoind.getNewAddress(walletNameOpt = Some(MintingWallet)))
    _ <- futureToIO(bitcoind.generateToAddress(numBlocks, addr))
  } yield println("Minted new block")

}
