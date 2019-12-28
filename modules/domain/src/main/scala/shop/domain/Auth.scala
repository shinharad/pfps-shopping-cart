package shop.domain

import java.util.UUID

import dev.profunktor.auth.jwt.JwtToken
import io.estatico.newtype.macros.newtype
import javax.crypto.Cipher

import scala.util.control.NoStackTrace

trait Auth[F[_]] {
  import Auth._
  def newUser(username: UserName, password: Password): F[JwtToken]
  def login(username: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, userName: UserName): F[Unit]
}

object Auth {

  @newtype case class UserId(value: UUID)
  @newtype case class UserName(value: String)
  @newtype case class Password(value: String)

  @newtype case class EncryptedPassword(value: String)

  @newtype case class EncryptCipher(value: Cipher)
  @newtype case class DecryptCipher(value: Cipher)

  case class UserNameInUse(username: UserName) extends NoStackTrace
  case class InvalidUserOrPassword(username: UserName) extends NoStackTrace

}
