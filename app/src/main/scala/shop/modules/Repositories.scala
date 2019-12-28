package shop.modules

import cats.Parallel
import cats.effect._
import cats.implicits._
import shop.domain.brand.Brands
import shop.domain.healthcheck.HealthCheck
//import dev.profunktor.redis4cats.algebra.RedisCommands
import shop.adapter.persistence._
//import shop.config.data._
import skunk._

object Repositories {
  def make[F[_]: Concurrent: Parallel: Timer](
      sessionPool: Resource[F, Session[F]]
  ): F[Repositories[F]] =
    for {
      brands <- LiveBrands.make[F](sessionPool)
      health <- LiveHealthCheck.make[F](sessionPool)
    } yield new Repositories(brands, health)

}

final class Repositories[F[_]] private (
    val brands: Brands[F],
    val healthCheck: HealthCheck[F]
)
