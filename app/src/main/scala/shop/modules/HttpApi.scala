package shop.modules

import cats.effect._
import cats.implicits._
import org.http4s.server.middleware._
//import dev.profunktor.auth.JwtAuthMiddleware
//import dev.profunktor.auth.jwt.JwtToken
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
//import pdi.jwt._
import shop.adapter.http.routes._
//import shop.http.auth.users._
//import shop.http.routes._
//import shop.http.routes.admin._
//import shop.http.routes.auth._
//import shop.http.routes.secured._

import scala.concurrent.duration._
import scala.language.postfixOps

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

//  private val adminMiddleware = JwtAuthMiddleware[F, AdminUser](security.adminJwtAuth.value, adminAuth)

  // Open routes
  private val healthRoutes   = new HealthRoutes[F](repos.healthCheck).routes
  private val brandRoutes    = new BrandRoutes[F](repos.brands).routes
  private val categoryRoutes = new CategoryRoutes[F](repos.categories).routes
  private val itemRoutes     = new ItemRoutes[F](repos.items).routes

  // Admin routes
//  private val adminBrandRoutes    = new AdminBrandRoutes[F](repos.brands).routes(adminMiddleware)

  // Combining all the http routes
  private val openRoutes: HttpRoutes[F] =
    healthRoutes <+> brandRoutes <+> categoryRoutes <+> itemRoutes

//  private val adminRoutes: HttpRoutes[F] =
//    adminItemRoutes

  private val routes: HttpRoutes[F] = Router(
    version.v1 -> openRoutes
  )

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http, CORS.DefaultCORSConfig)
    } andThen { http: HttpRoutes[F] =>
      Timeout(60 seconds)(http)
    }
  }

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(middleware(routes).orNotFound)

}
