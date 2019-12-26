import Dependencies.Libraries._
import Settings._
import ContainerSettings._

val basename = "shopping-cart"

ThisBuild / version := "0.1"

lazy val infrastructure = (project in file("modules/infrastructure"))
  .disablePlugins(RevolverPlugin)
  .settings(
    name := s"$basename-infrastructure",
    libraryDependencies ++= Seq(
      javaxCrypto
    )
  )
  .settings(coreSettings)

lazy val domain = (project in file("modules/domain"))
  .disablePlugins(RevolverPlugin)
  .settings(
    name := s"$basename-domain"
  )
  .settings(coreSettings)
  .dependsOn(infrastructure)

lazy val application = (project in file("modules/application"))
  .disablePlugins(RevolverPlugin)
  .settings(
    name := s"$basename-application"
  )
  .settings(coreSettings)
  .dependsOn(domain)

lazy val adapter = (project in file("modules/adapter"))
  .disablePlugins(RevolverPlugin)
  .settings(
    name := s"$basename-adapter",
    libraryDependencies ++= Seq(
      catsRetry,
      circeCore,
      circeGeneric,
      circeParser,
      circeRefined,
      http4sDsl,
      http4sServer,
      http4sClient,
      http4sCirce,
      http4sJwtAuth,
      skunkCore,
      skunkCirce,
      redis4catsEffects,
      redis4catsLog4cats
    )
  )
  .settings(coreSettings)
  .dependsOn(application)

lazy val app = (project in file("app"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)
  .settings(containerSettings)
  .settings(
    name := s"$basename-app",
    envVars in reStart := Map("SC_APP_ENV" -> "dev")
  )
  .settings(coreSettings)
  .dependsOn(adapter)

lazy val root = (project in file("."))
  .disablePlugins(RevolverPlugin)
  .settings(
    name := basename
  )
  .aggregate(
    infrastructure,
    domain,
    application,
    adapter,
    app
  )


