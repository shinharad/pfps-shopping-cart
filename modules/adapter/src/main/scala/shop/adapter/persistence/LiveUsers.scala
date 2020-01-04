package shop.adapter.persistence

import cats.effect._
import cats.implicits._
import shop.domain.Crypto._
import shop.domain._
import shop.domain.Users._
import shop.infrastructure.ErrorType.BracketThrow
import shop.adapter.persistence.skunkx._
import shop.infrastructure.effect.GenUUID
import skunk._
import skunk.codec.all._
import skunk.implicits._

object LiveUsers {
  def make[F[_]: Sync](
      sessionPool: Resource[F, Session[F]],
      crypto: Crypto
  ): F[Users[F]] =
    Sync[F].delay(
      new LiveUsers[F](sessionPool, crypto)
    )
}

final class LiveUsers[F[_]: BracketThrow: GenUUID] private (
    sessionPool: Resource[F, Session[F]],
    crypto: Crypto
) extends Users[F] {
  import UserQueries._

  def find(username: UserName, password: Password): F[Option[User]] =
    sessionPool.use { session =>
      session.prepare(selectUser).use { q =>
        q.option(username).map {
          case Some(u ~ p) if p.value == crypto.encrypt(password).value =>
            u.some
          case _ => none[User]
        }
      }
    }

  def create(username: UserName, password: Password): F[UserId] =
    sessionPool.use { session =>
      session.prepare(insertUser).use { cmd =>
        GenUUID[F].make[UserId].flatMap { id =>
          cmd
            .execute(User(id, username) ~ crypto.encrypt(password))
            .as(id)
            .handleErrorWith {
              case SqlState.UniqueViolation(_) =>
                UserNameInUse(username).raiseError[F, UserId]
            }
        }
      }
    }
}

private object UserQueries {

  val codec: Codec[User ~ EncryptedPassword] =
    (uuid.cimap[UserId] ~ varchar.cimap[UserName] ~ varchar.cimap[EncryptedPassword]).imap {
      case i ~ n ~ p =>
        User(i, n) ~ p
    } {
      case u ~ p =>
        u.id ~ u.name ~ p
    }

  val selectUser: Query[UserName, User ~ EncryptedPassword] =
    sql"""
         SELECT * FROM users
         WHERE name = ${varchar.cimap[UserName]}
       """.query(codec)

  val insertUser: Command[User ~ EncryptedPassword] =
    sql"""
         INSERT INTO users
         VALUES ($codec)
       """.command

}
