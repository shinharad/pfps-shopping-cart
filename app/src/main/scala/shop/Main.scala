package shop

import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.server.blaze.BlazeServerBuilder
import shop.modules._

object Main extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    config.loader[IO].flatMap { cfg =>
      Logger[IO].info(s"Loaded config $cfg") *>
        AppResources.make[IO](cfg).use { res =>
          for {
            repos <- Repositories.make[IO](res.psql)
            api <- HttpApi.make[IO](repos)
            _ <- BlazeServerBuilder[IO]
                  .bindHttp(8080, "localhost")
                  .withHttpApp(api.httpApp)
                  .serve
                  .compile
                  .drain
          } yield ExitCode.Success
        }
    }

}
