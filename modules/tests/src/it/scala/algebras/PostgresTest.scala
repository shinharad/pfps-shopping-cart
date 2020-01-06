package algebras

import cats.effect._
import cats.implicits._
import ciris._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.ops._
import natchez.Trace.Implicits.noop
import shop.adapter.persistence._
import shop.arbitraries._
import shop.domain.Brands._
import shop.domain.Categories._
import shop.domain.Items._
import shop.domain.Orders._
import shop.domain.ShoppingCart.CartItem
import shop.domain.Users._
import shop.infrastructure.config.data.PasswordSalt
import skunk.Session
import squants.market.Money
import suite.ResourceSuite

class PostgresTest extends ResourceSuite[Resource[IO, Session[IO]]] {

  // DBアクセスするケースなので1回成功すればOKとする
  val MaxTests: PropertyCheckConfigParam = MinSuccessful(1)

  lazy val salt = Secret("53kr3t": NonEmptyString).coerce[PasswordSalt]

  override def resources =
    Session.pooled[IO](
      host = "localhost",
      port = 5432,
      user = "postgres",
      database = "store",
      max = 10
    )

  withResources { pool =>
    forAll(MaxTests) { brand: Brand =>
      spec("Brands") {
        for {
          b <- LiveBrands.make[IO](pool)
          x <- b.findAll
          _ <- b.create(brand.name)
          y <- b.findAll
          z <- b.create(brand.name).attempt
        } yield assert(
          x.isEmpty && y.count(_.name.eqv(brand.name)).eqv(1) && z.isLeft
        )
      }
    }

    forAll(MaxTests) { (category: Category) =>
      spec("Categories") {
        for {
          c <- LiveCategories.make[IO](pool)
          x <- c.findAll
          _ <- c.create(category.name)
          y <- c.findAll
          z <- c.create(category.name).attempt
        } yield assert(
          x.isEmpty && y.count(_.name.eqv(category.name)).eqv(1) && z.isLeft
        )
      }
    }

    forAll(MaxTests) { (item: Item) =>
      spec("Items") {
        def newItem(
            bid: Option[BrandId],
            cid: Option[CategoryId]
        ) = CreateItem(
          name = item.name,
          description = item.description,
          price = item.price,
          brandId = bid.getOrElse(item.brand.id),
          categoryId = cid.getOrElse(item.category.uuid)
        )

        for {
          b <- LiveBrands.make[IO](pool)
          c <- LiveCategories.make[IO](pool)
          i <- LiveItems.make[IO](pool)
          x <- i.findAll
          _ <- b.create(item.brand.name)
          d <- b.findAll.map(_.headOption.map(_.id))
          _ <- c.create(item.category.name)
          e <- c.findAll.map(_.headOption.map(_.uuid))
          _ <- i.create(newItem(d, e))
          y <- i.findAll
        } yield assert(
          x.isEmpty && y.count(_.name.eqv(item.name)).eqv(1)
        )
      }
    }

    forAll(MaxTests) { (username: UserName, password: Password) =>
      spec("Users") {
        for {
          c <- LiveCrypto.make[IO](salt)
          u <- LiveUsers.make[IO](pool, c)
          d <- u.create(username, password)
          x <- u.find(username, password)
          y <- u.find(username, "foo".coerce[Password])
          z <- u.create(username, password).attempt
        } yield assert(
          x.count(_.id.eqv(d)).eqv(1) && y.isEmpty && z.isLeft
        )
      }
    }

    forAll(MaxTests) {
      (oid: OrderId, pid: PaymentId, un: UserName, pw: Password, items: List[CartItem], price: Money) =>
        spec("Orders") {
          for {
            o <- LiveOrders.make[IO](pool)
            c <- LiveCrypto.make[IO](salt)
            u <- LiveUsers.make[IO](pool, c)
            d <- u.create(un, pw)
            x <- o.findBy(d)
            y <- o.get(d, oid)
            i <- o.create(d, pid, items, price)
          } yield assert(
            x.isEmpty && y.isEmpty && i.value.version.eqv(4)
          )
        }
    }

  }

}
