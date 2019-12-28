package shop.application

import io.estatico.newtype.macros.newtype

trait HealthCheck[F[_]] {
  import HealthCheck._
  def status: F[AppStatus]
}

object HealthCheck {

  //  @newtype case class RedisStatus(value: Boolean)
  @newtype case class PostgresStatus(value: Boolean)

  case class AppStatus(
      //      redis: RedisStatus,
      postgresStatus: PostgresStatus
  )

}
