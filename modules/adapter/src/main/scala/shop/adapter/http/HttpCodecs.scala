package shop.adapter.http

import cats.effect.Sync
import cats.syntax.either._
import dev.profunktor.auth.jwt.JwtToken
import eu.timepit.refined._
import eu.timepit.refined.api._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import org.http4s._
import org.http4s.circe._
import shop.adapter.codec.CodecsUnderlying
import shop.adapter.http.request.brand.BrandParam
import shop.application.HealthCheck.AppStatus

object HttpCodecs extends CodecsUnderlying {

  implicit def jsonDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  implicit def jsonEncoder[F[_]: Sync, A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  // ----- Overriding some Coercible codecs ----
  implicit val brandParamDecoder: Decoder[BrandParam] =
    Decoder.forProduct1("name")(BrandParam.apply)

  //  implicit val categoryParamDecoder: Decoder[CategoryParam] =
  //    Decoder.forProduct1("name")(CategoryParam.apply)

  // ----- Coercible codecs -----
  implicit def coercibleQueryParamDecoder[A: Coercible[B, *], B: QueryParamDecoder]: QueryParamDecoder[A] =
    QueryParamDecoder[B].map(_.coerce[A])

  implicit def refinedQueryParamDecoder[T: QueryParamDecoder, P](
      implicit ev: Validate[T, P]
  ): QueryParamDecoder[T Refined P] =
    QueryParamDecoder[T].emap(refineV[P](_).leftMap(m => ParseFailure(m, m)))

  implicit val appStatusEncoder: Encoder[AppStatus] = deriveEncoder[AppStatus]

  // 手動のコーデック
  implicit val tokenEncoder: Encoder[JwtToken] = Encoder.forProduct1("access_token")(_.value)

}
