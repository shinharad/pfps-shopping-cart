package shop.application

import javax.smartcardio.Card
import shop.domain.Orders.PaymentId
import shop.domain.Users.UserId
import squants.market.Money

trait PaymentClient[F[_]] {
  def process(
      userId: UserId,
      total: Money,
      card: Card
  ): F[PaymentId]
}
