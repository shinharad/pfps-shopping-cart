package shop.modules

import cats.effect._
import cats.implicits._
import dev.profunktor.auth.jwt._
import dev.profunktor.redis4cats.algebra.RedisCommands
import pdi.jwt._
import shop.adapter.http.request.users._
import shop.adapter.persistence._
import shop.domain.Users._
import shop.domain._
import shop.infrastructure.config.data.AppConfig
import shop.infrastructure.effect.GenUUID
import skunk.Session

object Security {

  def make[F[_]: Sync](
      cfg: AppConfig,
      sessionPool: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String]
  ): F[Security[F]] = {

    val adminJwtAuth: AdminJwtAuth =
      AdminJwtAuth(
        JwtAuth
          .hmac(
            cfg.adminJwtConfig.secretKey.value.value.value,
            JwtAlgorithm.HS256
          )
      )

    val userJwtAuth: UserJwtAuth =
      UserJwtAuth(
        JwtAuth
          .hmac(
            cfg.tokenConfig.value.value.value,
            JwtAlgorithm.HS256
          )
      )

    val adminToken = JwtToken(cfg.adminJwtConfig.adminToken.value.value.value)

    for {
      adminClaim <- jwtDecode[F](adminToken, adminJwtAuth.value)
      content = adminClaim.content.replace("{", "0").replace("}", "c")
      adminId <- GenUUID[F].read[UserId](content)
      adminUser = AdminUser(User(adminId, UserName("admin")))
      tokens <- LiveTokens.make[F](cfg.tokenConfig, cfg.tokenExpiration)
      crypto <- LiveCrypto.make[F](cfg.passwordSalt)
      users <- LiveUsers.make[F](sessionPool, crypto)
      auth <- LiveAuth.make[F](cfg.tokenExpiration, tokens, users, redis)
      adminAuth <- LiveAdminAuth.make[F](adminToken, adminUser)
      usersAuth <- LiveUsersAuth.make[F](redis)
    } yield new Security[F](auth, adminAuth, usersAuth, adminJwtAuth, userJwtAuth)

  }
}

final class Security[F[_]] private (
    val auth: Auth[F],
    val adminAuth: UsersAuth[F, AdminUser],
    val usersAuth: UsersAuth[F, CommonUser],
    val adminJwtAuth: AdminJwtAuth,
    val userJwtAuth: UserJwtAuth
) {}
