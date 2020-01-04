package shop.adapter.persistence

import cats.effect.Sync
import cats.implicits._
import dev.profunktor.redis4cats.algebra.RedisCommands
import shop.domain.Items.ItemId
import shop.domain.ShoppingCart._
import shop.domain.Users.UserId
import shop.domain._
import shop.infrastructure.ErrorType._
import shop.infrastructure.config.data.ShoppingCartExpiration
import shop.infrastructure.effect.GenUUID
import squants.market._

object LiveShoppingCart {
  def make[F[_]: Sync](
      items: Items[F],
      redis: RedisCommands[F, String, String],
      exp: ShoppingCartExpiration
  ): F[ShoppingCart[F]] =
    Sync[F].delay(
      new LiveShoppingCart(items, redis, exp)
    )
}

final class LiveShoppingCart[F[_]: GenUUID: MonadThrow] private (
    items: Items[F],
    redis: RedisCommands[F, String, String],
    exp: ShoppingCartExpiration
) extends ShoppingCart[F] {

  def get(userId: UserId): F[CartTotal] =
    redis.hGetAll(userId.value.toString).flatMap { it =>
      it.toList
        .traverseFilter {
          case (k, v) =>
            for {
              id <- GenUUID[F].read[ItemId](k)
              qt <- ApThrow[F].catchNonFatal(Quantity(v.toInt))
              rs <- items.findById(id).map(_.map(i => CartItem(i, qt)))
            } yield rs
        }
        .map(items => CartTotal(items, calcTotal(items)))
    }

  private def calcTotal(items: List[CartItem]): Money =
    USD(
      items
        .foldMap { i =>
          i.item.price.value * i.quantity.value
        }
    )

  def add(userId: UserId, itemId: ItemId, quantity: Quantity): F[Unit] =
    redis.hSet(
      userId.value.toString,
      itemId.value.toString,
      quantity.value.toString
    ) *> redis.expire(userId.value.toString, exp.value)

  def delete(userId: UserId): F[Unit] =
    redis.del(userId.value.toString)

  def removeItem(userId: UserId, itemId: ItemId): F[Unit] =
    redis.hDel(userId.value.toString, itemId.value.toString)

  def update(userId: UserId, cart: Cart): F[Unit] =
    redis.hGetAll(userId.value.toString).flatMap { it =>
      // traverse_ は F[Unit]を返す
      //      def traverse[G[_], B](f : scala.Function1[C, G[B]])(implicit evidence$1 : cats.Applicative[G]) : G[F[B]] = { /* compiled code */ }
      //      def traverse_[G[_], B](f : scala.Function1[D, G[B]])(implicit G : cats.Applicative[G]) : G[scala.Unit] = { /* compiled code */ }
      it.toList.traverse_ {
        case (k, _) =>
          GenUUID[F].read[ItemId](k).flatMap { id =>
            cart.items.get(id).traverse_ { q =>
              redis.hSet(userId.value.toString, k, q.value.toString)
            }
          }
      }
    } *> redis.expire(userId.value.toString, exp.value)

}
