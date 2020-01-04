package shop.modules

import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import retry.RetryPolicies._
import retry.RetryPolicy
import shop.application.CheckoutProgram
import shop.infrastructure.ErrorType.MonadThrow
import shop.infrastructure.config.data.CheckoutConfig
import shop.infrastructure.effect.Background

object Programs {
  def make[F[_]: Background: Logger: Sync: Timer](
      checkoutConfig: CheckoutConfig,
      algebras: Algebras[F],
      clients: HttpClients[F]
  ): F[Programs[F]] =
    Sync[F].delay(
      new Programs[F](checkoutConfig, algebras, clients)
    )
}

final class Programs[F[_]: Background: Logger: MonadThrow: Timer] private (
    cfg: CheckoutConfig,
    algebras: Algebras[F],
    clients: HttpClients[F]
) {

  val retryPolicy: RetryPolicy[F] =
    limitRetries[F](cfg.retriesLimit.value) |+| exponentialBackoff[F](cfg.retriesBackoff)

  val checkout: CheckoutProgram[F] = new CheckoutProgram[F](
    clients.payment,
    algebras.cart,
    algebras.orders,
    retryPolicy
  )

}
