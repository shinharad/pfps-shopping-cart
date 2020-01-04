package shop.modules

import cats.effect._
import cats.implicits._
import dev.profunktor.auth.JwtAuthMiddleware
import dev.profunktor.auth.jwt.JwtToken
import org.http4s.server.middleware._
import shop.domain.Users.{ AdminUser, CommonUser }
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import pdi.jwt.JwtClaim
import shop.adapter.http.routes._
import shop.adapter.http.routes.admin._
import shop.adapter.http.routes.auth.{ LoginRoutes, LogoutRoutes, UserRoutes }

import scala.concurrent.duration._
import scala.language.postfixOps

object HttpApi {
  def make[F[_]: Concurrent: Timer](
      repos: Repositories[F],
      programs: Programs[F],
      security: Security[F]
  ): F[HttpApi[F]] =
    Sync[F].delay(
      new HttpApi[F](
        repos,
        programs,
        security
      )
    )
}

final class HttpApi[F[_]: Concurrent: Timer] private (
    repos: Repositories[F],
    @annotation.unused programs: Programs[F], // TODO
    security: Security[F]
) {

  private val adminAuth: JwtToken => JwtClaim => F[Option[AdminUser]] =
    t => c => security.adminAuth.findUser(t)(c)
  private val usersAuth: JwtToken => JwtClaim => F[Option[CommonUser]] =
    t => c => security.usersAuth.findUser(t)(c)

  private val adminMiddleware = JwtAuthMiddleware[F, AdminUser](security.adminJwtAuth.value, adminAuth)
  private val usersMiddleware = JwtAuthMiddleware[F, CommonUser](security.userJwtAuth.value, usersAuth)

  // Auth routes
  private val loginRoutes  = new LoginRoutes[F](security.auth).routes
  private val logoutRoutes = new LogoutRoutes[F](security.auth).routes(usersMiddleware)
  private val userRoutes   = new UserRoutes[F](security.auth).routes

  // Open routes
  private val healthRoutes   = new HealthRoutes[F](repos.healthCheck).routes
  private val brandRoutes    = new BrandRoutes[F](repos.brands).routes
  private val categoryRoutes = new CategoryRoutes[F](repos.categories).routes
  private val itemRoutes     = new ItemRoutes[F](repos.items).routes

  // Admin routes
  private val adminBrandRoutes    = new AdminBrandRoutes[F](repos.brands).routes(adminMiddleware)
  private val adminCategoryRoutes = new AdminCategoryRoutes[F](repos.categories).routes(adminMiddleware)
  private val adminItemRoutes     = new AdminItemRoutes[F](repos.items).routes(adminMiddleware)

  // Combining all the http routes
  private val openRoutes: HttpRoutes[F] =
    healthRoutes <+> brandRoutes <+> categoryRoutes <+> itemRoutes <+>
        loginRoutes <+> userRoutes

  private val signedInRoutes: HttpRoutes[F] =
    logoutRoutes

  private val adminRoutes: HttpRoutes[F] =
    adminBrandRoutes <+> adminCategoryRoutes <+> adminItemRoutes

  private val routes: HttpRoutes[F] = Router(
    version.v1 -> (openRoutes <+> signedInRoutes),
    version.v1 + "/admin" -> adminRoutes
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
