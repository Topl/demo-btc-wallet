package co.topl.btc.server.bitcoin

import cats.effect.IO
import cats.implicits._
import co.topl.btc.server.bitcoin.BitcoindExtended.futureToIO
import org.bitcoins.core.currency.Bitcoins
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.bitcoins.keymanager.WalletStorage
import org.bitcoins.crypto.AesPassword
import org.bitcoins.core.protocol.transaction._
import org.bitcoins.core.protocol.{BitcoinAddress, CompactSizeUInt}
import org.bitcoins.commons.jsonmodels.bitcoind.ListTransactionsResult
import org.bitcoins.core.number.UInt32

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
    _ <- if(allWallets.contains(DefaultWallet)) IO.unit else futureToIO(bitcoind.createWallet(DefaultWallet, descriptors=true))
    loadedWallets <- futureToIO(bitcoind.listWallets)
    unloadedWallets = allWallets.filterNot(loadedWallets.contains)
    res <- unloadedWallets.map(bitcoind.loadWallet).map(futureToIO).sequence
    _ <- Slf4jLogger.getLogger[IO].info("All wallets are loaded: " + (allWallets.toSet + MintingWallet + DefaultWallet).mkString(", "))
  } yield ()

  // Only valid for RegTest
  private def fundMintingWallet(bitcoind: BitcoindExtended): IO[Unit] = for {
    currentBalance <- futureToIO(bitcoind.getBalance(MintingWallet))
    needsFunding = currentBalance.toBigDecimal <= BigDecimal(InitialFunds)
    newBalance <- if(needsFunding) for {
      _ <- mintBlock(bitcoind, InitialBlocksToGenerate)
      balRes <- futureToIO(bitcoind.getBalance(MintingWallet)).iterateUntil(_.toBigDecimal >= BigDecimal(InitialFundsToMint))
    } yield balRes
     else IO.pure(currentBalance)
  _ <- Slf4jLogger.getLogger[IO].info("Minting wallet funded: " + newBalance)
  } yield ()

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
    _ <- Slf4jLogger.getLogger[IO].info("Default wallet funded: " + newBalance)
  } yield ()

  // Fund default wallet. Preconditions: Both wallets are loaded
  def fundWallets(bitcoind: BitcoindExtended): IO[Unit] = for {
    _ <- fundMintingWallet(bitcoind)
    _ <- fundDefaultWallet(bitcoind)
    _ <- Slf4jLogger.getLogger[IO].info("Both Minting and Default wallets are funded")
  } yield ()

  def mintBlock(bitcoind: BitcoindExtended, numBlocks: Int = 1): IO[Unit] = for {
    addr <- futureToIO(bitcoind.getNewAddress(walletNameOpt = Some(MintingWallet)))
    _ <- futureToIO(bitcoind.generateToAddress(numBlocks, addr))
    _ <- Slf4jLogger.getLogger[IO].info("Minted new block")
  } yield ()

  def getTxOutForAddress(bitcoind: BitcoindExtended, wallet: String, address: BitcoinAddress): IO[Option[(TransactionOutPoint, Bitcoins, Int)]] = for {
    res <- findTx(bitcoind, wallet, address, 10, 0)
    txOut = res.flatMap(txRes => (txRes.txid, txRes.vout, txRes.confirmations, txRes.amount) match {
      case (Some(txId), Some(vout), Some(conf), amount) => Some((TransactionOutPoint(txId, UInt32(vout)), Bitcoins((amount*(-1)).satoshis), conf)) // *(-1) since "send" category shows negative amount
      case _ => None
    })
  } yield txOut

  private def findTx(bitcoind: BitcoindExtended, wallet: String, address: BitcoinAddress, count: Int, skip: Int): IO[Option[ListTransactionsResult]] = {
    bitcoind.listWalletTransactions(wallet).flatMap(res => {
      val txOut = res.find(txRes => txRes.address.contains(address) && txRes.category == "send")
      if(txOut.isDefined) IO.pure(txOut) 
      else if(res.length < count) IO.pure(None)
      else findTx(bitcoind, wallet, address, count, skip + count)
    })
  }

}
