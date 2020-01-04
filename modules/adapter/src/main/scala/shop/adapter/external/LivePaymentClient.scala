package shop.adapter.external

import cats.effect.Sync
import cats.syntax.applicativeError._
import cats.syntax.either._
import cats.syntax.flatMap._
import org.http4s._
import org.http4s.client.Client
import shop.adapter.external.codecs._
import shop.application.PaymentClient
import shop.domain.Checkout.Card
import shop.domain.Orders._
import shop.domain.Users.UserId
import shop.infrastructure.config.data.PaymentConfig
import squants.market.Money

class LivePaymentClient[F[_]: Sync](
    cfg: PaymentConfig,
    client: Client[F]
) extends PaymentClient[F] {

  def process(userId: UserId, total: Money, card: Card): F[PaymentId] =
    Uri.fromString(cfg.uri.value.value + "/payments").liftTo[F].flatMap { uri =>
      client.get[PaymentId](uri) { r =>
        if (r.status == Status.Ok || r.status == Status.Conflict)
          r.as[PaymentId]
        else
          PaymentError(r.status.reason).raiseError[F, PaymentId]
      }
    }

}
