package shop.adapter.persistence

import cats.Parallel
import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import shop.domain.healthcheck.HealthCheck
//import dev.profunktor.redis4cats.algebra.RedisCommands
import shop.domain.healthcheck.data._
import skunk._
import skunk.codec.all._
import skunk.implicits._

import scala.concurrent.duration._
import scala.language.postfixOps

object LiveHealthCheck {
  def make[F[_]: Concurrent: Parallel: Timer](
      sessionPool: Resource[F, Session[F]]
//      redis: RedisCommands[F, String, String]
  ): F[HealthCheck[F]] =
    Sync[F].delay(
      new LiveHealthCheck[F](sessionPool)
//  new LiveHealthCheck[F](sessionPool, redis)
    )
}

final class LiveHealthCheck[F[_]: Concurrent: Parallel: Timer] private (
    sessionPool: Resource[F, Session[F]]
//    redis: RedisCommands[F, String, String]
) extends HealthCheck[F] {

  val q: Query[Void, Int] =
    sql"SELECT pid FROM pg_stat_activity".query(int4)

//  val redisHealth: F[RedisStatus] =
//    redis.ping
//      .map(_.nonEmpty)
//      .timeout(1 second)
//      .orElse(false.pure[F])
//      .map(RedisStatus.apply)

  val postgresHealth: F[PostgresStatus] =
    sessionPool
      .use(_.execute(q))
      .map(_.nonEmpty)
      .timeout(1 second)
      .orElse(false.pure[F])
      .map(PostgresStatus.apply)

  val status: F[AppStatus] =
    postgresHealth.map(AppStatus)
//    (redisHealth, postgresHealth).parMapN(AppStatus)

}
