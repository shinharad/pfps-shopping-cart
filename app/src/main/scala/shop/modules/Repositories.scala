package shop.modules

import cats.Parallel
import cats.effect._
import cats.implicits._
//import dev.profunktor.redis4cats.algebra.RedisCommands
import shop.domain._
import shop.adapter.persistence._
//import shop.config.data._
import skunk._
import shop.domain.Brands

object Repositories {
  def make[F[_]: Concurrent: Parallel: Timer](
      sessionPool: Resource[F, Session[F]]
  ): F[Repositories[F]] =
    for {
      brands <- LiveBrands.make[F](sessionPool)
    } yield new Repositories(brands)

}

final class Repositories[F[_]] private (
    val brands: Brands[F]
)
