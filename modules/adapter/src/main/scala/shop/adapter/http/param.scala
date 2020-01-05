package shop.adapter.http

import cats.syntax.either._
import eu.timepit.refined._
import eu.timepit.refined.api._
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import org.http4s._

object param {

  // ----- Coercible codecs -----
  implicit def coercibleQueryParamDecoder[A: Coercible[B, *], B: QueryParamDecoder]: QueryParamDecoder[A] =
    QueryParamDecoder[B].map(_.coerce[A])

  implicit def refinedQueryParamDecoder[T: QueryParamDecoder, P](
      implicit ev: Validate[T, P]
  ): QueryParamDecoder[T Refined P] =
    QueryParamDecoder[T].emap(refineV[P](_).leftMap(m => ParseFailure(m, m)))

}
