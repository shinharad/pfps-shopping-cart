import Dependencies.Libraries._

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
  .settings(CoreSettings.settings)

lazy val domain = (project in file("modules/domain"))
  .disablePlugins(RevolverPlugin)
  .settings(
    name := s"$basename-domain"
  )
  .settings(CoreSettings.settings)
  .dependsOn(infrastructure % "test->test;compile->compile")

lazy val application = (project in file("modules/application"))
  .disablePlugins(RevolverPlugin)
  .settings(
    name := s"$basename-application",
    libraryDependencies ++= Seq(
      catsRetry
    )
  )
  .settings(CoreSettings.settings)
  .dependsOn(domain % "test->test;compile->compile")

lazy val adapter = (project in file("modules/adapter"))
  .disablePlugins(RevolverPlugin)
  .settings(
    name := s"$basename-adapter",
    libraryDependencies ++= Seq(
      catsRetry, // TODO
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
  .settings(CoreSettings.settings)
  .dependsOn(application % "test->test;compile->compile")

lazy val app = (project in file("app"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)
  .settings(ContainerSettings.settings)
  .settings(
    name := s"$basename-app",
    envVars in reStart := Map(
      "SC_APP_ENV" -> "local",
      "SC_ACCESS_TOKEN_SECRET_KEY" -> "5h0pp1ng_k4rt",
      "SC_JWT_SECRET_KEY" -> "-*5h0pp1ng_k4rt*-",
      "SC_JWT_CLAIM" -> "{004b4457-71c3-4439-a1b2-03820263b59c}",
      "SC_ADMIN_USER_TOKEN" -> "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.ezA0YjQ0NTctNzFjMy00NDM5LWExYjItMDM4MjAyNjNiNTl9.mMC4eiPl7huiAO3GdORwKnqJrf96xKPoojQdZtrTbP4",
      "SC_PASSWORD_SALT" -> "06!grsnxXG0d*Pj496p6fuA*o",
    )
  )
  .settings(CoreSettings.settings)
  .dependsOn(adapter % "test->test;compile->compile")

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


