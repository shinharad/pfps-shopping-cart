package shop.adapter.http

import cats.syntax.either._
import cats.effect.Sync
import dev.profunktor.auth.jwt.JwtToken
import io.circe._
import io.circe.generic.semiauto._
import shop.adapter.http.request.users.User
import shop.domain.Categories.Category
import shop.domain.Items.Item
import eu.timepit.refined._
import eu.timepit.refined.api._
import io.circe.refined._
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import org.http4s.circe._
import org.http4s._
import shop.adapter.http.request.brand.BrandParam
import shop.application.HealthCheck.AppStatus
import shop.domain.Brands._
import squants.market._

object json {

  implicit def jsonDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  implicit def jsonEncoder[F[_]: Sync, A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  // ----- Overriding some Coercible codecs ----
  implicit val brandParamDecoder: Decoder[BrandParam] =
    Decoder.forProduct1("name")(BrandParam.apply)

  //  implicit val categoryParamDecoder: Decoder[CategoryParam] =
  //    Decoder.forProduct1("name")(CategoryParam.apply)

  // ----- Coercible codecs -----
  // @newtypeを使用する場合はこれが必要
  implicit def coercibleDecoder[A: Coercible[B, *], B: Decoder]: Decoder[A] =
    Decoder[B].map(_.coerce[A])

  implicit def coercibleEncoder[A: Coercible[B, *], B: Encoder]: Encoder[A] =
    Encoder[B].contramap(_.repr.asInstanceOf[B])

  // Mapのdecode/encodeに必要
  implicit def coercibleKeyDecoder[A: Coercible[B, *], B: KeyDecoder]: KeyDecoder[A] =
    KeyDecoder[B].map(_.coerce[A])

  implicit def coercibleKeyEncoder[A: Coercible[B, *], B: KeyEncoder]: KeyEncoder[A] =
    KeyEncoder[B].contramap[A](_.repr.asInstanceOf[B])

  implicit def coercibleQueryParamDecoder[A: Coercible[B, *], B: QueryParamDecoder]: QueryParamDecoder[A] =
    QueryParamDecoder[B].map(_.coerce[A])

  implicit def refinedQueryParamDecoder[T: QueryParamDecoder, P](
      implicit ev: Validate[T, P]
  ): QueryParamDecoder[T Refined P] =
    QueryParamDecoder[T].emap(refineV[P](_).leftMap(m => ParseFailure(m, m)))

  // ----- Domain codecs -----

  implicit val brandDecoder: Decoder[Brand] = deriveDecoder[Brand]
  implicit val brandEncoder: Encoder[Brand] = deriveEncoder[Brand]

  implicit val categoryDecoder: Decoder[Category] = deriveDecoder[Category]
  implicit val categoryEncoder: Encoder[Category] = deriveEncoder[Category]

  implicit val moneyDecoder: Decoder[Money] = Decoder[BigDecimal].map(USD.apply)
  implicit val moneyEncoder: Encoder[Money] = Encoder[BigDecimal].contramap(_.amount)

  implicit val itemDecoder: Decoder[Item] = deriveDecoder[Item]
  implicit val itemEncoder: Encoder[Item] = deriveEncoder[Item]

  //  implicit val createItemDecoder: Decoder[CreateItemParam] = deriveDecoder[CreateItemParam]
  //  implicit val updateItemDecoder: Decoder[UpdateItemParam] = deriveDecoder[UpdateItemParam]

  //  implicit val cartItemDecoder: Decoder[CartItem] = deriveDecoder[CartItem]
  //  implicit val cartItemEncoder: Encoder[CartItem] = deriveEncoder[CartItem]
  //
  //  implicit val cartTotalEncoder: Encoder[CartTotal] = deriveEncoder[CartTotal]
  //
  //  implicit val orderEncoder: Encoder[Order] = deriveEncoder[Order]
  //
  //  implicit val cardDecoder: Decoder[Card] = deriveDecoder[Card]

  // 手動のコーデック
  implicit val tokenEncoder: Encoder[JwtToken] = Encoder.forProduct1("access_token")(_.value)

  //  implicit val cartEncoder: Encoder[Cart] = Encoder.forProduct1("items")(_.items)
  //  implicit val cartDecoder: Decoder[Cart] = Decoder.forProduct1("items")(Cart.apply)
  //
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

  implicit val appStatusEncoder: Encoder[AppStatus] = deriveEncoder[AppStatus]

  //  implicit val createUserDecoder: Decoder[CreateUser] = deriveDecoder[CreateUser]
  //
  //  implicit val loginUserDecoder: Decoder[LoginUser] = deriveDecoder[LoginUser]

}
