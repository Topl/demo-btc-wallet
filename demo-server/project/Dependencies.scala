import sbt._

object Dependencies {
  lazy val http4sVersion = "0.23.23"
  lazy val catsCoreVersion = "2.10.0"
  lazy val catsEffectVersion = "3.5.1"
  lazy val log4catsVersion = "2.6.0"

  lazy val scalaTest: Seq[ModuleID] = Seq("org.scalatest" %% "scalatest" % "3.0.5" % Test)

  lazy val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-ember-client" % http4sVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion
  )

  lazy val cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % catsCoreVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion
  )
  
  lazy val log4cats: Seq[ModuleID] = Seq(
    "org.typelevel" %% "log4cats-core" % log4catsVersion,
    "org.typelevel" %% "log4cats-slf4j" % log4catsVersion
  )
}
