package shop.domain.healthcheck

import io.estatico.newtype.macros.newtype

trait HealthCheck[F[_]] {
  import data._
  def status: F[AppStatus]
}

object data {

  //  @newtype case class RedisStatus(value: Boolean)
  @newtype case class PostgresStatus(value: Boolean)

  case class AppStatus(
      //      redis: RedisStatus,
      postgresStatus: PostgresStatus
  )

}
