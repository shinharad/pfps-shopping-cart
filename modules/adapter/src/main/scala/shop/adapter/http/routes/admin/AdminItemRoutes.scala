package shop.adapter.http.routes.admin

import cats.effect.Sync
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import shop.adapter.http.decoder._
import shop.adapter.http.codecs._
import shop.adapter.http.request.items._
import shop.domain.Items
import shop.domain.Users.AdminUser

final class AdminItemRoutes[F[_]: Sync](
    items: Items[F]
) extends Http4sDsl[F] {

  private[admin] val prefixPath = "/items"

  private val httpRoutes: AuthedRoutes[AdminUser, F] =
    AuthedRoutes.of {
      // Create new item
      case ar @ POST -> Root as _ =>
        ar.req.decodeR[CreateItemParam] { item =>
          Created(items.create(item.toDomain))
        }

      case ar @ PUT -> Root as _ =>
        ar.req.decodeR[UpdateItemParam] { item =>
          Ok(items.update(item.toDomain))
        }
    }

  def routes(authMiddleware: AuthMiddleware[F, AdminUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
