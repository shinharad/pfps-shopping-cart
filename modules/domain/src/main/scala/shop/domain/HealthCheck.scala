package shop.domain

import io.estatico.newtype.macros.newtype

object HealthCheck {

//  @newtype case class RedisStatus(value: Boolean)
  @newtype case class PostgresStatus(value: Boolean)

  case class AppStatus(
//      redis: RedisStatus,
      postgresStatus: PostgresStatus
  )

}

trait HealthCheck[F[_]] {
  import HealthCheck._
  def status: F[AppStatus]
}
