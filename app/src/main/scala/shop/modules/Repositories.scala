package shop.modules

import cats.Parallel
import cats.effect._
import cats.implicits._
import shop.domain.{ Brands, Categories, HealthCheck }
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
      categories <- LiveCategories.make[F](sessionPool)
      health <- LiveHealthCheck.make[F](sessionPool)
    } yield new Repositories(brands, categories, health)

}

final class Repositories[F[_]] private (
    val brands: Brands[F],
    val categories: Categories[F],
    val healthCheck: HealthCheck[F]
)
