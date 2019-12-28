package shop.support

//import java.util.UUID
//
//import eu.timepit.refined.api.Refined
//import io.estatico.newtype.Coercible
//import io.estatico.newtype.ops._
//import org.scalacheck.Gen
//import shop.domain.brand._
//import shop.domain.cart._
//import shop.domain.category._
//import shop.domain.checkout._
//import shop.domain.item._
//import squants.market._

object generators {

//  def cbUuid[A: Coercible[UUID, *]]: Gen[A] =
//    Gen.uuid.map(_.coerce[A])
//
//  def cbStr[A: Coercible[String, *]]: Gen[A] =
//    genNonEmptyString.map(_.coerce[A])
//
//  def cbInt[A: Coercible[Int, *]]: Gen[A] =
//    Gen.posNum[Int].map(_.coerce[A])
//
//  def cbBigDecimal[A: Coercible[BigDecimal, *]]: Gen[A] =
//    Gen.posNum[Long].map(n => BigDecimal(n).coerce[A])
//
//  val genMoney: Gen[Money] =
//    Gen.posNum[Long].map(n => USD(BigDecimal(n)))
//
//  //  val genNonEmptyString: Gen[String] =
//  //    Gen.alphaStr.suchThat(_.nonEmpty)
//
//  // 生成する文字を限定してテストデータを高速に生成する
//  val genNonEmptyString: Gen[String] =
//    Gen
//      .chooseNum(21, 40)
//      .flatMap { n =>
//        Gen.buildableOfN[String, Char](n, Gen.alphaChar)
//      }
//
//  val brandGen: Gen[Brand] =
//    for {
//      i <- cbUuid[BrandId]
//      n <- cbStr[BrandName]
//    } yield Brand(i, n)
//
//  val categoryGen: Gen[Category] =
//    for {
//      i <- cbUuid[CategoryId]
//      n <- cbStr[CategoryName]
//    } yield Category(i, n)
//
//  val itemGen: Gen[Item] =
//    for {
//      i <- cbUuid[ItemId]
//      n <- cbStr[ItemName]
//      d <- cbStr[ItemDescription]
//      p <- genMoney
//      b <- brandGen
//      c <- categoryGen
//    } yield Item(i, n, d, p, b, c)
//
//  val cartItemGen: Gen[CartItem] =
//    for {
//      i <- itemGen
//      q <- cbInt[Quantity]
//    } yield CartItem(i, q)
//
//  val cartTotalGen: Gen[CartTotal] =
//    for {
//      i <- Gen.nonEmptyListOf(cartItemGen)
//      t <- genMoney
//    } yield CartTotal(i, t)
//
//  val itemMapGen: Gen[(ItemId, Quantity)] =
//    for {
//      i <- cbUuid[ItemId]
//      q <- cbInt[Quantity]
//    } yield i -> q
//
//  val cartGen: Gen[Cart] =
//    Gen.nonEmptyMap(itemMapGen).map(Cart.apply)
//
//  // Refined.unsafeApplyはテストのみに使用する
//  val cardGen: Gen[Card] =
//    for {
//      n <- genNonEmptyString.map[CardNamePred](Refined.unsafeApply)
//      u <- Gen.posNum[Long].map[CardNumberPred](Refined.unsafeApply)
//      x <- Gen.posNum[Int].map[CardExpirationPred](x => Refined.unsafeApply(x.toString))
//      c <- Gen.posNum[Int].map[CardCCVPred](Refined.unsafeApply)
//    } yield Card(CardName(n), CardNumber(u), CardExpiration(x), CardCCV(c))

}
