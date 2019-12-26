package shop.config

//import ciris.Secret
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
//import io.estatico.newtype.macros.newtype

//import scala.concurrent.duration.FiniteDuration

object data {

  case class AppConfig(
      postgreSQL: PostgreSQLConfig
  )

  case class PostgreSQLConfig(
      host: NonEmptyString,
      port: UserPortNumber,
      user: NonEmptyString,
      database: NonEmptyString,
      max: PosInt
  )

}
