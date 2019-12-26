package shop.modules

import cats.effect._
//import cats.implicits._
//import dev.profunktor.auth.JwtAuthMiddleware
//import dev.profunktor.auth.jwt.JwtToken
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
//import org.http4s.server.middleware._
//import pdi.jwt._
import shop.adapter.http.routes._
//import shop.http.auth.users._
//import shop.http.routes._
//import shop.http.routes.admin._
//import shop.http.routes.auth._
//import shop.http.routes.secured._

//import scala.concurrent.duration._

object HttpApi {
  def make[F[_]: Concurrent: Timer](
      repos: Repositories[F]
  ): F[HttpApi[F]] =
    Sync[F].delay(
      new HttpApi[F](
        repos
      )
    )
}

final class HttpApi[F[_]: Concurrent: Timer] private (
    repos: Repositories[F]
) {

  // Open routes
  private val brandRoutes = new BrandRoutes[F](repos.brands).routes

  // Combining all the http routes
  private val openRoutes: HttpRoutes[F] =
    brandRoutes

  private val routes: HttpRoutes[F] = Router(
    version.v1 -> openRoutes
  )

  val httpApp: HttpApp[F] = routes.orNotFound

}
