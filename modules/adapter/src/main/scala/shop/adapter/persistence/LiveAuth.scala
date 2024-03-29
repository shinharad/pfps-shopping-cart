package shop.adapter.persistence

import cats.effect.Sync
import cats.implicits._
import cats.{ Applicative, Functor }
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.algebra.RedisCommands
import io.circe.parser.decode
import io.circe.syntax._
import pdi.jwt.JwtClaim
import shop.adapter.persistence.codecs._
import shop.domain.Users._
import shop.domain._
import shop.infrastructure.ErrorType.MonadThrow
import shop.infrastructure.config.data.TokenExpiration
import shop.infrastructure.effect.GenUUID

object LiveAuth {
  def make[F[_]: Sync](
      tokenExpiration: TokenExpiration,
      tokens: Tokens[F],
      users: Users[F],
      redis: RedisCommands[F, String, String]
  ): F[LiveAuth[F]] =
    Sync[F].delay(
      new LiveAuth(tokenExpiration, tokens, users, redis)
    )
}

final class LiveAuth[F[_]: GenUUID: MonadThrow] private (
    tokenExpiration: TokenExpiration,
    tokens: Tokens[F],
    users: Users[F],
    redis: RedisCommands[F, String, String]
) extends Auth[F] {

  private val TokenExpiration = tokenExpiration.value

  def newUser(username: UserName, password: Password): F[JwtToken] =
    users.find(username, password).flatMap {
      case Some(_) => UserNameInUse(username).raiseError[F, JwtToken]
      case None =>
        for {
          i <- users.create(username, password)
          t <- tokens.create
          u = User(i, username).asJson.noSpaces
          _ <- redis.setEx(t.value, u, TokenExpiration)
          _ <- redis.setEx(username.value, t.value, TokenExpiration)
        } yield t
    }

  def login(username: UserName, password: Password): F[JwtToken] =
    users.find(username, password).flatMap {
      case None => InvalidUserOrPassword(username).raiseError[F, JwtToken]
      case Some(user) =>
        redis.get(username.value).flatMap {
          case Some(t) => JwtToken(t).pure[F]
          case None =>
            tokens.create.flatTap { t =>
              redis.setEx(t.value, user.asJson.noSpaces, TokenExpiration) *>
                redis.setEx(username.value, t.value, TokenExpiration)
            }
        }
    }

  def logout(token: JwtToken, username: UserName): F[Unit] =
    redis.del(token.value) *> redis.del(username.value)

}

object LiveUsersAuth {
  def make[F[_]: Sync](
      redis: RedisCommands[F, String, String]
  ): F[UsersAuth[F, CommonUser]] =
    Sync[F].delay(
      new LiveUsersAuth(redis)
    )
}

class LiveUsersAuth[F[_]: Functor](
    redis: RedisCommands[F, String, String]
) extends UsersAuth[F, CommonUser] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[CommonUser]] =
    redis
      .get(token.value)
      .map(_.flatMap { u =>
        decode[User](u).toOption.map(CommonUser.apply)
      })
}

object LiveAdminAuth {
  def make[F[_]: Sync](
      adminToken: JwtToken,
      adminUser: AdminUser
  ): F[UsersAuth[F, AdminUser]] =
    Sync[F].delay(
      new LiveAdminAuth(adminToken, adminUser)
    )
}

class LiveAdminAuth[F[_]: Applicative](
    adminToken: JwtToken,
    adminUser: AdminUser
) extends UsersAuth[F, AdminUser] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[AdminUser]] =
    (token == adminToken)
      .guard[Option]
      .as(adminUser)
      .pure[F]
}
