package shop.domain

import java.util.UUID

import io.estatico.newtype.macros.newtype
import shop.domain.Items._
import shop.domain.Users.UserId
import squants.market.Money

import scala.util.control.NoStackTrace

trait ShoppingCart[F[_]] {
  import ShoppingCart._
  def get(userId: UserId): F[CartTotal]
  def add(userId: UserId, itemId: ItemId, quantity: Quantity): F[Unit]
  def delete(userId: UserId): F[Unit]
  def removeItem(userId: UserId, itemId: ItemId): F[Unit]
  def update(userId: UserId, cart: Cart): F[Unit]
}

object ShoppingCart {

  @newtype case class Quantity(value: Int)
  @newtype case class Cart(items: Map[ItemId, Quantity])
  @newtype case class CartId(value: UUID)

  case class CartItem(item: Item, quantity: Quantity)
  case class CartTotal(items: List[CartItem], total: Money)

  case class CartNotFound(userId: UserId) extends NoStackTrace

}
