package shop.adapter.external

import cats.effect.Sync
import io.circe._
import org.http4s.circe._
import org.http4s._
import shop.adapter.codec.CodecsUnderlying

object codecs extends CodecsUnderlying {

  implicit def jsonDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

}
