import Dependencies._
import scala.sys.process.Process

lazy val scalacVersion = "2.13.12"

inThisBuild(
  List(
    homepage := Some(url("https://github.com/Topl/demo-btc-wallet")),
    licenses := Seq("MPL2.0" -> url("https://www.mozilla.org/en-US/MPL/2.0/")),
    scalaVersion := "2.12.18"
  )
)

lazy val noPublish = Seq(
  publishLocal / skip := true,
  publish / skip := true
)

val importClient = taskKey[Unit]("Import client (frontend)")

importClient := {
  // Copy vite output into server resources, where it can be accessed by the server,
  // even after the server is packaged in a fat jar.
  IO.copyDirectory(
    source = (root / baseDirectory).value / ".." /  "demo-ui" / "dist",
    target = (root / baseDirectory).value / "src" / "main" / "resources" / "static"
  )
}

lazy val root = project
  .in(file("."))
  .settings(
    scalaVersion := scalacVersion,
    organization := "co.topl",
    name := "topl-demo-btc-wallet",
    libraryDependencies ++= http4s ++ cats ++ log4cats ++ slf4j ++ circe ++ btc ++ scopt
    )
    .settings(noPublish)

// Development mode: reloads the server when you change the code. Use "sbt dev" to run.
addCommandAlias("dev", "importClient ; ~reStart")