package shop.domain

import io.estatico.newtype.macros.newtype
import shop.domain.Auth._

trait Users[F[_]] {
  import Users._
  def find(username: UserName, password: Password): F[Option[User]]
  def create(username: UserName, password: Password): F[UserId]
}

object Users {

  case class User(id: UserId, name: UserName)

  @newtype case class CommonUser(value: User)
  @newtype case class AdminUser(value: User)

}
