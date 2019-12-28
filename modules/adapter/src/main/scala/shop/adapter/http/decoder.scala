package shop.adapter.http

import cats.Monad
import cats.implicits._
import cats.syntax.either._
import eu.timepit.refined._
import eu.timepit.refined.api._
import io.estatico.newtype.Coercible
import io.estatico.newtype.ops._
import org.http4s._
import org.http4s.dsl.Http4sDsl

object decoder {

  // これは、Refinedからの検証エラーを処理し、
  // デフォルトの応答コード422（処理不能なエンティティ）の代わりにエラーメッセージとともに
  // 応答コード400（Bad Request）を返すカスタムデコード関数です
  implicit class RefinedRequestDecoder[F[_]: Monad](req: Request[F]) extends Http4sDsl[F] {
    def decodeR[A](f: A => F[Response[F]])(implicit ev: EntityDecoder[F, A]): F[Response[F]] =
      ev.decode(req, strict = false).value.flatMap {
        case Left(e) =>
          e.cause match {
            case Some(c) if c.getMessage.startsWith("Predicate") =>
              BadRequest(c.getMessage)
            case _ =>
              UnprocessableEntity()
          }
        case Right(a) => f(a)
      }
  }

  implicit def coercibleQueryParamDecoder[A: Coercible[B, *], B: QueryParamDecoder]: QueryParamDecoder[A] =
    QueryParamDecoder[B].map(_.coerce[A])

  implicit def refinedQueryParamDecoder[T: QueryParamDecoder, P](
      implicit ev: Validate[T, P]
  ): QueryParamDecoder[T Refined P] =
    QueryParamDecoder[T].emap(refineV[P](_).leftMap(m => ParseFailure(m, m)))

}
