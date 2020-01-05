package shop.adapter.codec

import io.circe._
import io.circe.generic.semiauto._
//import shop.domain.Checkout.Card
import shop.domain.Brands._
import shop.domain.Categories.Category
import shop.domain.Items.Item
import shop.domain.Orders.Order
import shop.domain.ShoppingCart._
import shop.domain.Users.User
import squants.market._

trait UnderlyingCodecs extends NewtypeCodecs {

  // ----- Domain codecs -----

  implicit val brandDecoder: Decoder[Brand] = deriveDecoder[Brand]
  implicit val brandEncoder: Encoder[Brand] = deriveEncoder[Brand]

  implicit val categoryDecoder: Decoder[Category] = deriveDecoder[Category]
  implicit val categoryEncoder: Encoder[Category] = deriveEncoder[Category]

  implicit val moneyDecoder: Decoder[Money] = Decoder[BigDecimal].map(USD.apply)
  implicit val moneyEncoder: Encoder[Money] = Encoder[BigDecimal].contramap(_.amount)

  implicit val itemDecoder: Decoder[Item] = deriveDecoder[Item]
  implicit val itemEncoder: Encoder[Item] = deriveEncoder[Item]

  implicit val cartItemDecoder: Decoder[CartItem] = deriveDecoder[CartItem]
  implicit val cartItemEncoder: Encoder[CartItem] = deriveEncoder[CartItem]

  implicit val cartTotalEncoder: Encoder[CartTotal] = deriveEncoder[CartTotal]

  implicit val orderEncoder: Encoder[Order] = deriveEncoder[Order]

  // TODO codecのエラー
//  implicit val cardDecoder: Decoder[Card] = deriveDecoder[Card]

  implicit val cartEncoder: Encoder[Cart] = Encoder.forProduct1("items")(_.items)
  implicit val cartDecoder: Decoder[Cart] = Decoder.forProduct1("items")(Cart.apply)

  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

}
