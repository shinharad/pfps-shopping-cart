import sbt._
import sbt.Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._
import Dependencies.Libraries._

object Settings {

  val coreSettings: Def.SettingsDefinition = Seq(
    organization := "com.github.shinharad",
    scalaVersion := "2.13.1",
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding", "UTF-8",
      "-Ymacro-annotations",
      "-Xlint"
//      "-Xfatal-warnings",
//      "-language:_",
//      // Warn if an argument list is modified to match the receiver
//      "-Ywarn-adapted-args",
//      // Warn when dead code is identified.
//      "-Ywarn-dead-code",
//      // Warn about inaccessible types in method signatures.
//      "-Ywarn-inaccessible",
//      // Warn when a type argument is inferred to be `Any`.
//      "-Ywarn-infer-any",
//      // Warn when non-nullary `def f()' overrides nullary `def f'
//      "-Ywarn-nullary-override",
//      // Warn when nullary methods return Unit.
//      "-Ywarn-nullary-unit",
//      // Warn when numerics are widened.
//      "-Ywarn-numeric-widen",
//      // Warn when imports are unused.
//      "-Ywarn-unused-import"
    ),
    scalafmtOnCompile := true,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      compilerPlugin(betterMonadicFor),
      compilerPlugin(kindProjector),
      cats,
      catsEffect,
      cirisCore,
      cirisEnum,
      cirisRefined,
      refinedCore,
      refinedCats,
      newtype,
      squants,
      log4cats,
      logback,
      scalaCheck,
      scalaTest,
      scalaTestPlus
    )
  )

}
