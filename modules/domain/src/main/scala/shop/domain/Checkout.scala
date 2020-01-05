package shop.domain

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.Size
import eu.timepit.refined.string.{ MatchesRegex, ValidInt }
import io.estatico.newtype.macros.newtype

object Checkout {

  type Rgx = W.`"^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$"`.T

  type CardNamePred       = String Refined MatchesRegex[Rgx]
  type CardNumberPred     = Long Refined Size[16]
  type CardExpirationPred = String Refined (Size[4] And ValidInt)
  type CardCCVPred        = Int Refined Size[3]

  // TODO codeのエラーをどうにかする
  @newtype case class CardName(value: String)
  @newtype case class CardNumber(value: Long)
  @newtype case class CardExpiration(value: String)
  @newtype case class CardCCV(value: Int)

//  @newtype case class CardName(value: CardNamePred)
//  @newtype case class CardNumber(value: CardNumberPred)
//  @newtype case class CardExpiration(value: CardExpirationPred)
//  @newtype case class CardCCV(value: CardCCVPred)

  case class Card(
      name: CardName,
      number: CardNumber,
      expiration: CardExpiration,
      ccv: CardCCV
  )

}
