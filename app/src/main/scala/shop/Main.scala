package shop

import cats.effect._
import cats.syntax.functor._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
//import shop.modules._

object Main extends IOApp with Http4sDsl[IO] {

  implicit val logger = Slf4jLogger.getLogger[IO]

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
