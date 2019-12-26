package shop

import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {

  implicit val logger = Slf4jLogger.getLogger[IO]

//  override def run(args: List[String]): IO[ExitCode] =
//    config.loader[IO].flatMap { cfg =>
//      Logger[IO].info(s"Loaded config $cfg") *>
//        AppResources.make[IO](cfg).use { res =>
//          for {
//            repos <- Repositories.make[IO](res.psql)
//            api <- HttpApi.make[IO](repos)
//            _ <- BlazeServerBuilder[IO]
//                  .bindHttp(8080, "localhost")
//                  .withHttpApp(api.httpApp)
//                  .serve
//                  .compile
//                  .drain
//          } yield ExitCode.Success
//        }
//    }

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

  // dummy
  val service = HttpRoutes
    .of[IO] {
      case _ =>
        Ok("Ok response.")
      //        IO(Response(Status.Ok))
    }
    .orNotFound

}
