package shop.domain

import java.util.UUID

import io.estatico.newtype.macros.newtype

import scala.util.control.NoStackTrace

trait Users[F[_]] {
  import Users._
  def find(username: UserName, password: Password): F[Option[User]]
  def create(username: UserName, password: Password): F[UserId]
}

object Users {

  @newtype case class UserId(value: UUID)
  @newtype case class UserName(value: String)
  @newtype case class Password(value: String)

  case class User(id: UserId, name: UserName)

  @newtype case class CommonUser(value: User)
  @newtype case class AdminUser(value: User)

  case class UserNameInUse(username: UserName) extends NoStackTrace
  case class InvalidUserOrPassword(username: UserName) extends NoStackTrace

}
