package shop.adapter.http.routes

import cats.effect.Sync
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import shop.adapter.http.decoder._
import shop.domain.Brands._
import shop.domain.Items
import shop.adapter.http.json._

final class ItemRoutes[F[_]: Sync](items: Items[F]) extends Http4sDsl[F] {
  import ItemRoutes._

  private[routes] val prefixPath = "/items"

  object BrandQueryParam extends OptionalQueryParamDecoderMatcher[BrandParam]("brand")

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? BrandQueryParam(brand) =>
      Ok(brand.fold(items.findAll)(b => items.findBy(b.toDomain)))
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}

object ItemRoutes {

  // TODO
  @newtype case class BrandParam(value: NonEmptyString) {
    def toDomain: BrandName = BrandName(value.value.toLowerCase.capitalize)
  }

}
