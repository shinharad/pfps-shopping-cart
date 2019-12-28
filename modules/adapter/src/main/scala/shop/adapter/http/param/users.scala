package shop.adapter.http.param

import dev.profunktor.auth.jwt.JwtSymmetricAuth
import io.estatico.newtype.macros.newtype
import shop.domain.Auth._

object users {

  @newtype case class AdminJwtAuth(value: JwtSymmetricAuth)
  @newtype case class UserJwtAuth(value: JwtSymmetricAuth)

  case class User(id: UserId, name: UserName)

  @newtype case class CommonUser(value: User)
  @newtype case class AdminUser(value: User)

}
