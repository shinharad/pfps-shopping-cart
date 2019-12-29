package shop.adapter.http.routes.admin

import cats.effect.Sync
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import shop.domain.Brands
import shop.adapter.http.decoder._
import shop.adapter.http.HttpCodecs._
import shop.adapter.http.request.brand.BrandParam
import shop.domain.Users.AdminUser

final class AdminBrandRoutes[F[_]: Sync](
    brands: Brands[F]
) extends Http4sDsl[F] {

  private[admin] val prefixPath = "/brands"

  private val httpRoutes: AuthedRoutes[AdminUser, F] =
    AuthedRoutes.of {
      case ar @ POST -> Root as _ =>
        ar.req.decodeR[BrandParam] { bp =>
          Created(brands.create(bp.toDomain))
        }
    }

  def routes(
      authMiddleware: AuthMiddleware[F, AdminUser]
  ): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
