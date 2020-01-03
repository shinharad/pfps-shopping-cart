package shop.adapter.http.routes

import cats.effect._
import cats.implicits._
import org.http4s.Method._
import org.http4s._
import org.http4s.client.dsl.io._
import shop.domain.Brands
import shop.domain.Brands._
import shop.adapter.http.codecs._
import shop.suite.HttpTestSuite
import shop.support.arbitraries._

class BrandRoutesSpec extends HttpTestSuite {

  def dataBrands(brands: List[Brand]): TestBrands = new TestBrands {
    override def findAll: IO[List[Brand]] =
      IO.pure(brands)
  }

  def failingBrands(brands: List[Brand]): TestBrands = new TestBrands {
    override def findAll: IO[List[Brand]] =
      IO.raiseError(DummyError) *> IO.pure(brands)
  }

  forAll { b: List[Brand] =>
    spec("GET brands [OK]") {
      GET(Uri.uri("/brands")).flatMap { req =>
        val routes = new BrandRoutes[IO](dataBrands(b)).routes
        assertHttp(routes, req)(Status.Ok, b)
      }
    }
  }

  forAll { b: List[Brand] =>
    spec("GET brands [ERROR]") {
      GET(Uri.uri("/brands")).flatMap { req =>
        val routes = new BrandRoutes[IO](failingBrands(b)).routes
        assertHttpFailure(routes, req)
      }
    }
  }

}

protected class TestBrands extends Brands[IO] {
  def create(name: BrandName): IO[Unit] = IO.unit
  def findAll: IO[List[Brand]]          = IO.pure(List.empty)
}
