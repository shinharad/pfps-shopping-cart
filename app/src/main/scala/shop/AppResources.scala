package shop

import cats.effect._
//import cats.implicits._ // TODO
import shop.infrastructure.config.data._
//import dev.profunktor.redis4cats.algebra.RedisCommands
//import dev.profunktor.redis4cats.connection.{ RedisClient, RedisURI }
//import dev.profunktor.redis4cats.domain.RedisCodec
//import dev.profunktor.redis4cats.interpreter.Redis
//import dev.profunktor.redis4cats.log4cats._
import io.chrisdavenport.log4cats.Logger
import natchez.Trace.Implicits.noop // needed for skunk
//import org.http4s.client.Client
//import org.http4s.client.blaze.BlazeClientBuilder
//import scala.concurrent.ExecutionContext
import skunk._

final case class AppResources[F[_]](
    psql: Resource[F, Session[F]]
)

object AppResources {
  def make[F[_]: ConcurrentEffect: ContextShift: Logger](
      cfg: AppConfig
  ): Resource[F, AppResources[F]] = {

    def mkPostgreSqlResource(c: PostgreSQLConfig): SessionPool[F] =
      Session
        .pooled[F](
          host = c.host.value,
          port = c.port.value,
          user = c.user.value,
          database = c.database.value,
          max = c.max.value
        )

    mkPostgreSqlResource(cfg.postgreSQL).map(AppResources.apply[F])

  }
}
