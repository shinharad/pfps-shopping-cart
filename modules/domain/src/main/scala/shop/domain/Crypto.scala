package shop.domain

import io.estatico.newtype.macros.newtype
import javax.crypto.Cipher
import shop.domain.Users._

trait Crypto {
  import Crypto._
  def encrypt(value: Password): EncryptedPassword
  def decrypt(value: EncryptedPassword): Password
}

object Crypto {

  @newtype case class EncryptedPassword(value: String)

  @newtype case class EncryptCipher(value: Cipher)
  @newtype case class DecryptCipher(value: Cipher)

}
