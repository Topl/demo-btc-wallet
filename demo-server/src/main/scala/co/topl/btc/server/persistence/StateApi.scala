package co.topl.btc.server.persistence

import cats.effect.IO

trait StateApi {
  /* Initialize the persistence layer upon application start. Does nothing if the StateApi is already initialized */
  def init(): IO[Unit]

  /* Stores a Bitcoin Escrow Address along with Script, along with the index that was used to derive the containing public key */
  def storeEscrowInfo(address: String, script: String, idx: Int): IO[Unit]


  /* Retrieve the script and index that is associated to an address. Used to derive the signature */
  def getInfoForAddress(address: String): IO[Option[(Int, String)]]

  /* Retrieve the next available index to use */
  def getNextIndex(): IO[Int]
}
