import sbt._
import sbt.Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._
import Dependencies.Libraries._

object CoreSettings {

  val settings: Def.SettingsDefinition = Seq(
    organization := "com.github.shinharad",
    scalaVersion := "2.13.1",
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding", "UTF-8",
      "-Ymacro-annotations",
      "-Xlint"
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
