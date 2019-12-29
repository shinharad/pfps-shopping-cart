package shop.domain

import dev.profunktor.auth.jwt._

trait Tokens[F[_]] {
  def create: F[JwtToken]
}
