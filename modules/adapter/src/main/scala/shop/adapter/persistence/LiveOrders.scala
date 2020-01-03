package shop.adapter.persistence

import cats.effect._
import cats.syntax.flatMap._
import cats.syntax.functor._
import shop.domain.Items.ItemId
import shop.domain.Orders
import shop.domain.Orders._
import shop.domain.ShoppingCart._
import shop.domain.Users.UserId
import shop.infrastructure.GenUUID
import skunk._
import skunk.circe.codec.all._
import skunk.codec.all._
import skunk.implicits._
import squants.market._
import shop.adapter.persistence.codecs._
import shop.adapter.persistence.skunkx._

object LiveOrders {
  def make[F[_]: Sync](
      sessionPool: Resource[F, Session[F]]
  ): F[Orders[F]] =
    Sync[F].delay(
      new LiveOrders[F](sessionPool)
    )
}

private class LiveOrders[F[_]: Sync](
    sessionPool: Resource[F, Session[F]]
) extends Orders[F] {
  import OrderQueries._

  def get(userId: UserId, orderId: OrderId): F[Option[Order]] =
    sessionPool.use { session =>
      session.prepare(selectByUserIdAndOrderId).use { q =>
        q.option(userId ~ orderId)
      }
    }

  def findBy(userId: UserId): F[List[Order]] =
    sessionPool.use { session =>
      session.prepare(selectByUserId).use { q =>
        q.stream(userId, 1024).compile.toList
      }
    }

  def create(userId: UserId, paymentId: PaymentId, items: List[CartItem], total: Money): F[OrderId] =
    sessionPool.use { session =>
      session.prepare(insertOrder).use { cmd =>
        GenUUID[F].make[OrderId].flatMap { id =>
          val itMap = items.map(x => x.item.uuid -> x.quantity).toMap
          val order = Order(id, paymentId, itMap, total)
          cmd.execute(userId ~ order).as(id)
        }
      }
    }

}

private object OrderQueries {

  val decoder: Decoder[Order] =
    (
      uuid.cimap[OrderId] ~ uuid ~ uuid.cimap[PaymentId] ~
          jsonb[Map[ItemId, Quantity]] ~ numeric.map(USD.apply)
    ).map {
      case o ~ _ ~ p ~ i ~ t =>
        Order(o, p, i, t)
    }

  val encoder: Encoder[UserId ~ Order] =
    (
      uuid.cimap[OrderId] ~ uuid.cimap[UserId] ~ uuid.cimap[PaymentId] ~
          jsonb[Map[ItemId, Quantity]] ~ numeric.contramap[Money](_.amount)
    ).contramap {
      case id ~ o =>
        o.id ~ id ~ o.paymentId ~ o.items ~ o.total
    }

  val selectByUserId: Query[UserId, Order] =
    sql"""
         SELECT * FROM orders
         WHERE user_id = ${uuid.cimap[UserId]}
       """.query(decoder)

  val selectByUserIdAndOrderId: Query[UserId ~ OrderId, Order] =
    sql"""
         SELECT * FROM orders
         WHERE user_id = ${uuid.cimap[UserId]}
         AND uuid = ${uuid.cimap[OrderId]}
       """.query(decoder)

  val insertOrder: Command[UserId ~ Order] =
    sql"""
         INSERT INTO orders
         VALUES ($encoder)
       """.command

}
