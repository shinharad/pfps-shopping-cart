package shop.adapter.http.routes

import cats.effect._
import cats.implicits._
import org.http4s.Method._
import org.http4s._
import org.http4s.client.dsl.io._
import shop.adapter.http.HttpCodecs._
import shop.domain.Brands._
import shop.domain.Items._
import shop.domain._
import shop.suite.HttpTestSuite
import shop.support.arbitraries._

class ItemRoutesSpec extends HttpTestSuite {

  def dataItems(items: List[Item]): TestItems = new TestItems {
    override def findAll: IO[List[Item]] =
      IO.pure(items)
  }

  def failingItems(items: List[Item]): TestItems = new TestItems {
    override def findAll: IO[List[Item]] =
      IO.raiseError(DummyError) *> IO.pure(items)
    override def findBy(brand: BrandName): IO[List[Item]] =
      findAll
  }

  forAll { it: List[Item] =>
    spec("GET items [OK]") {
      GET(Uri.uri("/items")).flatMap { req =>
        val routes = new ItemRoutes[IO](dataItems(it)).routes
        assertHttp(routes, req)(Status.Ok, it)
      }
    }
  }

  forAll { (it: List[Item], b: Brand) =>
    spec("GET items by brand [OK]") {
      GET(Uri.uri("/items").withQueryParam(b.name.value)).flatMap { req =>
        val routes = new ItemRoutes[IO](dataItems(it)).routes
        assertHttp(routes, req)(Status.Ok, it)
      }
    }
  }

}

protected class TestItems extends Items[IO] {
  def findAll: IO[List[Item]]                    = IO.pure(List.empty)
  def findBy(brand: BrandName): IO[List[Item]]   = IO.pure(List.empty)
  def findById(itemId: ItemId): IO[Option[Item]] = IO.pure(none[Item])
  def create(item: CreateItem): IO[Unit]         = IO.unit
  def update(item: UpdateItem): IO[Unit]         = IO.unit
}
