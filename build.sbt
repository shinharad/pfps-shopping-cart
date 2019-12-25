import Dependencies.Libraries._
import Settings._

val basename = "shopping-cart"

lazy val root = (project in file("."))
  .settings(
    name := basename
  )
//  .settings(coreSettings)
  .aggregate(
    infrastructure,
    domain,
    application,
    adapter,
    app
  )

lazy val infrastructure = (project in file("modules/infrastructure"))
  .settings(
    name := s"$basename-infrastructure",
    libraryDependencies ++= Seq(
      javaxCrypto
    )
  )
  .settings(coreSettings)

lazy val domain = (project in file("modules/domain"))
  .settings(
    name := s"$basename-domain",
//    libraryDependencies ++= Seq.empty
  )
  .settings(coreSettings)
  .dependsOn(infrastructure)

lazy val application = (project in file("modules/application"))
  .settings(
    name := s"$basename-application",
//    libraryDependencies ++= Seq.empty
  )
  .settings(coreSettings)
  .dependsOn(domain)

lazy val adapter = (project in file("modules/adapter"))
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
  .settings(
    name := s"$basename-app",
//    libraryDependencies ++= Seq.empty
  )
  .settings(coreSettings)
  .dependsOn(adapter)
