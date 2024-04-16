import com.typesafe.sbt.packager.docker._
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

lazy val commonDockerSettings = List(
  Docker / version := dynverGitDescribeOutput.value
    .mkVersion(versionFmt, fallbackVersion(dynverCurrentDate.value)),
  dockerAliases := dockerAliases.value.flatMap { alias =>
    if (sys.env.get("RELEASE_PUBLISH").getOrElse("false").toBoolean)
      Seq(
        alias.withRegistryHost(Some("ghcr.io/topl")),
        alias.withRegistryHost(Some("docker.io/toplprotocol"))
      )
    else
      Seq(
        alias.withRegistryHost(Some("ghcr.io/topl"))
      )
  },
  dockerBaseImage := "adoptopenjdk/openjdk11:jdk-11.0.16.1_1-ubuntu",
  dockerChmodType := DockerChmodType.UserGroupWriteExecute,
  dockerUpdateLatest := true
)

lazy val dockerPublishSettings = List(
  dockerExposedPorts ++= Seq(3002),
  Docker / packageName := "demo-btc-wallet"
) ++ commonDockerSettings

def versionFmt(out: sbtdynver.GitDescribeOutput): String = {
  val dirtySuffix = out.dirtySuffix.dropPlus.mkString("-", "")
  if (out.isCleanAfterTag)
    out.ref.dropPrefix + dirtySuffix // no commit info if clean after tag
  else
    out.ref.dropPrefix + out.commitSuffix.mkString("-", "-", "") + dirtySuffix
}

def fallbackVersion(d: java.util.Date): String =
  s"HEAD-${sbtdynver.DynVer timestamp d}"

val buildClient = taskKey[Unit]("Build client (frontend)")

buildClient := {

  // Install JS dependencies from package-lock.json
  val npmCiExitCode = Process("npm ci", cwd = (root / baseDirectory).value / ".." / "demo-ui").!
  if (npmCiExitCode > 0) {
    throw new IllegalStateException(s"npm ci failed. See above for reason")
  }

  // Build the frontend with vite
  val buildExitCode =
    Process("npm run build", cwd = (root / baseDirectory).value / ".." / "demo-ui").!
  if (buildExitCode > 0) {
    throw new IllegalStateException(
      s"Building frontend failed. See above for reason"
    )
  }

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
    dockerPublishSettings,
    scalaVersion := scalacVersion,
    organization := "co.topl",
    name := "topl-demo-btc-wallet",
    libraryDependencies ++= http4s ++ cats ++ log4cats ++ slf4j ++ circe ++ btc ++ scopt
  )
  .enablePlugins(DockerPlugin, JavaAppPackaging)
  .settings(noPublish)

// Development mode: reloads the server when you change the code. Use "sbt dev" to run.
addCommandAlias("dev", "buildClient ; ~reStart --btc-user=test --btc-password=test")