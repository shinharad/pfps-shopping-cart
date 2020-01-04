package shop.application

import cats.effect.Timer
import cats.syntax.applicativeError._
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.monadError._
import io.chrisdavenport.log4cats.Logger
import javax.smartcardio.Card
import retry.RetryDetails.{ GivingUp, WillDelayAndRetry }
import retry._
import shop.domain.Orders._
import shop.domain.ShoppingCart._
import shop.domain.Users.UserId
import shop.domain.{ Orders, ShoppingCart }
import shop.infrastructure.ErrorType.MonadThrow
import shop.infrastructure.effect.Background
import squants.market.Money

import scala.concurrent.duration._
import scala.language.postfixOps

final class CheckoutProgram[F[_]: Background: MonadThrow: Logger: Timer](
    paymentClient: PaymentClient[F],
    shoppingCart: ShoppingCart[F],
    orders: Orders[F],
    retryPolicy: RetryPolicy[F]
) {

  def checkout(userId: UserId, card: Card): F[OrderId] =
    shoppingCart
      .get(userId)
      .ensure(EmptyCartError)(_.items.nonEmpty)
      .flatMap {
        case CartTotal(items, total) =>
          for {
            pid <- processPayment(userId, total, card)
            order <- createOrder(userId, pid, items, total)
            _ <- shoppingCart.delete(userId).attempt.void
          } yield order
      }

  private def logError(action: String)(e: Throwable, details: RetryDetails): F[Unit] =
    details match {
      case r: WillDelayAndRetry =>
        Logger[F].error(
          s"Failed to process $action with ${e.getMessage}. So far we have retried ${r.retriesSoFar} times."
        )
      case g: GivingUp =>
        Logger[F].error(s"Giving up on $action after ${g.totalRetries} retries.")
    }

  // 再試行を考慮した決済処理
  def processPayment(
      userId: UserId,
      total: Money,
      card: Card
  ): F[PaymentId] = {
    val action: F[PaymentId] = retryingOnAllErrors[PaymentId](
      policy = retryPolicy,
      onError = logError("Payments")
    )(paymentClient.process(userId, total, card))

    action.adaptError {
      case e => PaymentError(e.getMessage)
    }
  }

  // 再試行と重複リクエストを考慮した注文処理
  def createOrder(
      userId: UserId,
      paymentId: PaymentId,
      items: List[CartItem],
      total: Money
  ): F[OrderId] = {
    val action = retryingOnAllErrors[OrderId](
      policy = retryPolicy,
      onError = logError("Order")
    )(orders.create(userId, paymentId, items, total))

    action
      .adaptError {
        case e => OrderError(e.getMessage)
      }
      .onError {
        case _ =>
          Logger[F].error(s"Failed to create order for Payment: ${paymentId}. Rescheduling as a background action") *>
              Background[F].schedule(action, 1 hour)
      }
  }

}
