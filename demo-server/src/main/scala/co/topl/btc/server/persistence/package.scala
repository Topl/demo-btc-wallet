package co.topl.btc.server

import cats.effect.kernel.Resource
import cats.effect.IO

import java.sql.{Connection, DriverManager}

package object persistence {
  /**
   * Creates a resource that provides a connection to a wallet state database.
   *
   * @param name the name of the file containing the wallet state database. It might be a path if needed.
   * @return a resource that provides a connection to a wallet state database.
   */
  def connection(name: String): Resource[IO, Connection] = Resource
    .make(
      IO.delay(
        DriverManager.getConnection(
          s"jdbc:sqlite:${name}"
        )
      )
    )(conn => IO.delay(conn.close()))

  /*
  * Creates an instance of StateApi that is connected to a SQLITE database
  */
  def connect(name: String): StateApi = {
    val connResource = connection(name)
    new StateApi {
      def init(): IO[Unit] = connResource.use { conn =>
        for {
          stmt <- IO.delay(conn.createStatement())
          _ <- IO.delay(stmt.execute("CREATE TABLE IF NOT EXISTS state (id INTEGER PRIMARY KEY, address TEXT NOT NULL, idx INTEGER NOT NULL)"))
          _ <- IO.delay(stmt.execute("CREATE UNIQUE INDEX IF NOT EXISTS addresses ON state (address)"))
          _ <- IO.delay(stmt.execute("CREATE UNIQUE INDEX IF NOT EXISTS indices ON state (idx)"))
          _ <- IO.delay(stmt.close())
        } yield ()
      }

      def storeEscrowAddress(address: String, idx: Int): IO[Unit] = connResource.use { conn =>
        for {
          stmt <- IO.delay(conn.createStatement())
          _ <- IO.delay(stmt.executeUpdate(s"INSERT INTO state (address, idx) VALUES ('$address', $idx)"))
          _ <- IO.delay(stmt.close())
        } yield ()
      }

      def getIndexForAddress(address: String): IO[Option[Int]] = connResource.use { conn =>
        for {
          stmt <- IO.delay(conn.createStatement())
          rs <- IO.delay(stmt.executeQuery(s"SELECT idx FROM state WHERE address = '$address'"))
          idx <- IO.delay(rs.getInt("idx"))
          _ <- IO.delay(stmt.close())
        } yield if(rs.next()) Some(idx) else None
      }

      def getNextIndex(): IO[Int] = connResource.use { conn =>
        for {
          stmt <- IO.delay(conn.createStatement())
          rs <-  IO.delay(stmt.executeQuery("SELECT MAX(idx) + 1 AS max_idx FROM state"))
          idx <- IO.delay(rs.getInt("max_idx")) // if the value is SQL NULL, the value returned is 0
          _ <- IO.delay(stmt.close())
        } yield idx
      }
    }
  }
}

