package co.topl.btc.server.persistence

import cats.effect.IO

trait StateApi {
  /* Initialize the persistence layer upon application start. Does nothing if the StateApi is already initialized */
  def init(): IO[Unit]

  /* Stores a Bitcoin Escrow Address, along with the index that was used to derive the containing public key */
  def storeEscrowAddress(address: String, idx: Int): IO[Unit]


  /* Retrieve the index that is associated to an address. Used to derive the private key */
  def getIndexForAddress(address: String): IO[Option[Int]]

  /* Retrieve the next available index to use */
  def getNextIndex(): IO[Int]
}
