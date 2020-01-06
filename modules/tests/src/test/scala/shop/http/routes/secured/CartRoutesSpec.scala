package shop.http.routes.secured

import java.util.UUID

import cats.data.Kleisli
import cats.effect._
import org.http4s.Method._
import org.http4s._
import org.http4s.client.dsl.io._
import org.http4s.server.AuthMiddleware
import shop.adapter.http.codecs._
import shop.adapter.http.routes.secured.CartRoutes
import shop.domain.Items.ItemId
import shop.domain.ShoppingCart
import shop.domain.ShoppingCart._
import shop.domain.Users._
import shop.arbitraries._
import squants.market.USD
import suite.HttpTestSuite

class CartRoutesSpec extends HttpTestSuite {

  val authUser = CommonUser(User(UserId(UUID.randomUUID), UserName("user")))

  val authMiddleware: AuthMiddleware[IO, CommonUser] =
    AuthMiddleware(Kleisli.pure(authUser))

  def dataCart(cartTotal: CartTotal) = new TestShoppingCart {
    override def get(userId: UserId): IO[CartTotal] =
      IO.pure(cartTotal)
  }

  forAll { (ct: CartTotal) =>
    spec("GET shopping cart [OK]") {
      GET(Uri.uri("/cart")).flatMap { req =>
        val routes = new CartRoutes[IO](dataCart(ct)).routes(authMiddleware)
        assertHttp(routes, req)(Status.Ok, ct)
      }
    }
  }

  forAll { (c: Cart) =>
    spec("POST add item to shopping cart [OK]") {
      POST(c, Uri.uri("/cart")).flatMap { req =>
        val routes = new CartRoutes[IO](new TestShoppingCart).routes(authMiddleware)
        assertHttpStatus(routes, req)(Status.Created)
      }
    }
  }
}

protected class TestShoppingCart extends ShoppingCart[IO] {
  def add(userId: UserId, itemId: ItemId, quantity: Quantity): IO[Unit] = IO.unit
  def get(userId: UserId): IO[CartTotal] =
    IO.pure(CartTotal(List.empty, USD(0)))
  def delete(userId: UserId): IO[Unit]                     = IO.unit
  def removeItem(userId: UserId, itemId: ItemId): IO[Unit] = IO.unit
  def update(userId: UserId, cart: Cart): IO[Unit]         = IO.unit
}
