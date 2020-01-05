package shop.adapter.http.routes.secured

import cats.effect.Sync
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import shop.application.CheckoutProgram
import shop.domain.Checkout.Card
import shop.domain.Orders._
import shop.domain.ShoppingCart.CartNotFound
import shop.domain.Users.CommonUser
import shop.adapter.http.decoder._
import shop.adapter.http.codecs._

final class CheckoutRoutes[F[_]: Sync](
    program: CheckoutProgram[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/checkout"

  private val httpRoutes: AuthedRoutes[CommonUser, F] =
    AuthedRoutes.of {
      case ar @ POST -> Root as user =>
        ar.req.decodeR[Card] { card =>
          program
            .checkout(user.value.id, card)
            .flatMap(Created(_))
            .recoverWith {
              case CartNotFound(userId) =>
                NotFound(s"Cart not found for user: ${userId.value}")
              case EmptyCartError =>
                BadRequest("Shopping cart is empty!")
              case PaymentError(cause) =>
                BadRequest(cause)
              case OrderError(cause) =>
                BadRequest(cause)
            }
        }
    }

  def routes(
      authMiddleware: AuthMiddleware[F, CommonUser]
  ): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
