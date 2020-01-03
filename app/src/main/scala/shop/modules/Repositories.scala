package shop.modules

import cats.Parallel
import cats.effect._
import cats.implicits._
import dev.profunktor.redis4cats.algebra.RedisCommands
import shop.application.HealthCheck
import shop.domain._
import shop.adapter.persistence._
import skunk._

object Repositories {
  def make[F[_]: Concurrent: Parallel: Timer](
      redis: RedisCommands[F, String, String],
      sessionPool: Resource[F, Session[F]]
  ): F[Repositories[F]] =
    for {
      brands <- LiveBrands.make[F](sessionPool)
      categories <- LiveCategories.make[F](sessionPool)
      items <- LiveItems.make[F](sessionPool)
      orders <- LiveOrders.make[F](sessionPool)
      health <- LiveHealthCheck.make[F](sessionPool, redis)
    } yield new Repositories(brands, categories, items, orders, health)

}

final class Repositories[F[_]] private (
    val brands: Brands[F],
    val categories: Categories[F],
    val items: Items[F],
    val orders: Orders[F],
    val healthCheck: HealthCheck[F]
)
