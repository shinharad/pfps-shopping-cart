package shop.adapter.http

import cats.effect.Sync
import dev.profunktor.auth.jwt.JwtToken
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._
import org.http4s._
import org.http4s.circe._
import shop.adapter.codec.UnderlyingCodecs
import shop.adapter.http.request.brand.BrandParam
import shop.adapter.http.request.category.CategoryParam
import shop.adapter.http.request.items._
import shop.adapter.http.request.users._
import shop.application.HealthCheck.AppStatus

object codecs extends UnderlyingCodecs {

  implicit def jsonDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  implicit def jsonEncoder[F[_]: Sync, A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  // ----- Overriding some Coercible codecs ----
  implicit val brandParamDecoder: Decoder[BrandParam] =
    Decoder.forProduct1("name")(BrandParam.apply)

  implicit val categoryParamDecoder: Decoder[CategoryParam] =
    Decoder.forProduct1("name")(CategoryParam.apply)

  implicit val createItemDecoder: Decoder[CreateItemParam] = deriveDecoder[CreateItemParam]
  implicit val updateItemDecoder: Decoder[UpdateItemParam] = deriveDecoder[UpdateItemParam]

  implicit val createUserDecoder: Decoder[CreateUser] = deriveDecoder[CreateUser]
  implicit val loginUserDecoder: Decoder[LoginUser]   = deriveDecoder[LoginUser]

  implicit val appStatusEncoder: Encoder[AppStatus] = deriveEncoder[AppStatus]

  // 手動のコーデック
  implicit val tokenEncoder: Encoder[JwtToken] = Encoder.forProduct1("access_token")(_.value)

}
