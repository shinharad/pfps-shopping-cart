package shop.config

import cats.effect._
//import cats.implicits._
import ciris._
//import ciris.refined._
import environments._
import environments.AppEnvironment._
import eu.timepit.refined.auto._
//import eu.timepit.refined.cats._
//import eu.timepit.refined.types.string.NonEmptyString
//import scala.concurrent.duration._
import shop.config.data._

object loader {

  def apply[F[_]: Async: ContextShift]: F[AppConfig] =
    env("SC_APP_ENV")
      .as[AppEnvironment]
      .flatMap {
        case Dev =>
          default()
        case Prod =>
          default()
      }
      .load[F]

  private def default(): ConfigValue[AppConfig] =
    ConfigValue.default(
      AppConfig(
        PostgreSQLConfig(
          host = "localhost",
          port = 5432,
          user = "postgres",
          database = "store",
          max = 10
        )
      )
    )

}
