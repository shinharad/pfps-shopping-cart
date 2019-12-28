package shop.adapter.persistence

import cats.effect._
import cats.syntax.flatMap._
import cats.syntax.functor._
import shop.domain.Categories
import shop.domain.Categories._
import shop.infrastructure.GenUUID
import shop.adapter.persistence.skunkx._
import shop.infrastructure.error.BracketThrow
import skunk._
import skunk.codec.all._
import skunk.implicits._

object LiveCategories {
  def make[F[_]: Sync](
      sessionPool: Resource[F, Session[F]]
  ): F[Categories[F]] =
    Sync[F].delay(
      new LiveCategories[F](sessionPool)
    )
}

final class LiveCategories[F[_]: BracketThrow: GenUUID] private (
    sessionPool: Resource[F, Session[F]]
) extends Categories[F] {
  import CategoryQueries._

  def findAll: F[List[Category]] =
    sessionPool.use(_.execute(selectAll))

  def create(name: CategoryName): F[Unit] =
    sessionPool.use { session =>
      session.prepare(insertCategory).use { cmd =>
        GenUUID[F].make[CategoryId].flatMap { id =>
          cmd.execute(Category(id, name)).void
        }
      }
    }
}

object CategoryQueries {

  val codec: Codec[Category] =
    (uuid.cimap[CategoryId] ~ varchar.cimap[CategoryName]).imap {
      case i ~ n => Category(i, n)
    }(c => c.uuid ~ c.name)

  val selectAll: Query[Void, Category] =
    sql"""
         SELECT * FROM categories
       """.query(codec)

  val insertCategory: Command[Category] =
    sql"""
         INSERT INTO categories
         VALUES ($codec)
       """.command

}
