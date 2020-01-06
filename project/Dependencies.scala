import sbt._

object Dependencies {

  object Versions {
    val cats          = "2.0.0"
    val catsEffect    = "2.0.0"
    val catsRetry     = "1.0.0"
    val circe         = "0.12.3"
    val ciris         = "1.0.3"
    val console4cats  = "0.8.0"
    val javaxCrypto   = "1.0.1"
    val http4s        = "0.21.0-M6"
    val http4sJwtAuth = "0.0.3"
    val log4cats      = "1.0.1"
    val newtype       = "0.4.3"
    val refined       = "0.9.10"
    val redis4cats    = "0.9.1"
    val skunk         = "0.0.7"
    val squants       = "1.6.0"

    val betterMonadicFor = "0.3.1"
    val kindProjector    = "0.11.0"
    val logback          = "1.2.3"

    val scalaCheck    = "1.14.3"
    val scalaTest     = "3.1.0"
    val scalaTestPlus = "3.1.0.0"
  }

  object Libraries {

    val cats       = "org.typelevel"    %% "cats-core"   % Versions.cats
    val catsEffect = "org.typelevel"    %% "cats-effect" % Versions.catsEffect
    val catsRetry  = "com.github.cb372" %% "cats-retry"  % Versions.catsRetry

    // circe
    def circe(artifact: String): ModuleID = "io.circe" %% artifact % Versions.circe
    val circeCore                         = circe("circe-core")
    val circeGeneric                      = circe("circe-generic")
    val circeParser                       = circe("circe-parser")
    val circeRefined                      = circe("circe-refined")

    // ciris
    def ciris(artifact: String): ModuleID = "is.cir" %% artifact % Versions.ciris
    val cirisCore                         = ciris("ciris")
    val cirisEnum                         = ciris("ciris-enumeratum")
    val cirisRefined                      = ciris("ciris-refined")

    // http4s
    def http4s(artifact: String): ModuleID = "org.http4s" %% artifact % Versions.http4s
    val http4sDsl                          = http4s("http4s-dsl")
    val http4sServer                       = http4s("http4s-blaze-server")
    val http4sClient                       = http4s("http4s-blaze-client")
    val http4sCirce                        = http4s("http4s-circe")

    val http4sJwtAuth = "dev.profunktor" %% "http4s-jwt-auth" % Versions.http4sJwtAuth

    val refinedCore  = "eu.timepit"     %% "refined"      % Versions.refined
    val refinedCats  = "eu.timepit"     %% "refined-cats" % Versions.refined
    val newtype      = "io.estatico"    %% "newtype"      % Versions.newtype
    val squants      = "org.typelevel"  %% "squants"      % Versions.squants
    val console4cats = "dev.profunktor" %% "console4cats" % Versions.console4cats

    val log4cats    = "io.chrisdavenport" %% "log4cats-slf4j" % Versions.log4cats
    val javaxCrypto = "javax.xml.crypto"  % "jsr105-api"      % Versions.javaxCrypto

    val skunkCore  = "org.tpolecat" %% "skunk-core"  % Versions.skunk
    val skunkCirce = "org.tpolecat" %% "skunk-circe" % Versions.skunk

    val redis4catsEffects  = "dev.profunktor" %% "redis4cats-effects"  % Versions.redis4cats
    val redis4catsLog4cats = "dev.profunktor" %% "redis4cats-log4cats" % Versions.redis4cats

    // Compiler plugins
    val betterMonadicFor = "com.olegpy"    %% "better-monadic-for" % Versions.betterMonadicFor
    val kindProjector    = "org.typelevel" %% "kind-projector"     % Versions.kindProjector cross CrossVersion.full

    // Runtime
    val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    // Test
    val scalaCheck    = "org.scalacheck"    %% "scalacheck"      % Versions.scalaCheck
    val scalaTest     = "org.scalatest"     %% "scalatest"       % Versions.scalaTest
    val scalaTestPlus = "org.scalatestplus" %% "scalacheck-1-14" % Versions.scalaTestPlus

  }

}
