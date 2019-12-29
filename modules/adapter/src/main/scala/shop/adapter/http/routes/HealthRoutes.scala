package shop.adapter.http.routes

import cats.effect.Sync
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import shop.adapter.http.HttpCodecs._
import shop.application.HealthCheck

final class HealthRoutes[F[_]: Sync](
    healthCheck: HealthCheck[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/healthcheck"

  private val httpRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(healthCheck.status)
    }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}
