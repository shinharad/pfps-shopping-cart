package shop.domain

import java.util.UUID

import io.estatico.newtype.macros.newtype
import shop.domain.Items.ItemId
import shop.domain.ShoppingCart.{CartItem, Quantity}
import shop.domain.Users.UserId
import squants.market.Money

import scala.util.control.NoStackTrace

trait Orders[F[_]] {
  import Orders._
  def get(userId: UserId, orderId: OrderId): F[Option[Order]]
  def findBy(userId: UserId): F[List[Order]]
  def create(userId: UserId, paymentId: PaymentId, items: List[CartItem], total: Money): F[OrderId]
}

object Orders {

  @newtype case class OrderId(value: UUID)
  @newtype case class PaymentId(value: UUID)

  case class Order(
      id: OrderId,
      paymentId: PaymentId,
      items: Map[ItemId, Quantity],
      total: Money
  )

  case object EmptyCartError extends NoStackTrace
  case class OrderError(cause: String) extends NoStackTrace
  case class PaymentError(cause: String) extends NoStackTrace

}
