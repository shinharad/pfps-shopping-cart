package shop.support

import java.util.UUID

import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import org.scalacheck.{ Arbitrary, Gen }
import shop.domain.Brands.Brand
import shop.domain.Categories.Category
import shop.domain.Checkout.Card
import shop.domain.Items.Item
import shop.domain.ShoppingCart._
import shop.support.generators._
import squants.market.Money

object arbitraries {

  implicit def arbCoercibleInt[A: Coercible[Int, *]]: Arbitrary[A] =
    Arbitrary(Gen.posNum[Int].map(_.coerce[A]))

  implicit def arbCoercibleStr[A: Coercible[String, *]]: Arbitrary[A] =
    Arbitrary(cbStr[A])

  implicit def arbCoercibleUUID[A: Coercible[UUID, *]]: Arbitrary[A] =
    Arbitrary(cbUuid[A])

  implicit val arbBrand: Arbitrary[Brand] =
    Arbitrary(brandGen)

  implicit val arbCategory: Arbitrary[Category] =
    Arbitrary(categoryGen)

  implicit val arbMoney: Arbitrary[Money] =
    Arbitrary(genMoney)

  implicit val arbItem: Arbitrary[Item] =
    Arbitrary(itemGen)

  implicit val arbCartItem: Arbitrary[CartItem] =
    Arbitrary(cartItemGen)

  implicit val arbCartTotal: Arbitrary[CartTotal] =
    Arbitrary(cartTotalGen)

  implicit val arbCart: Arbitrary[Cart] =
    Arbitrary(cartGen)

  implicit val arbCard: Arbitrary[Card] =
    Arbitrary(cardGen)

}
