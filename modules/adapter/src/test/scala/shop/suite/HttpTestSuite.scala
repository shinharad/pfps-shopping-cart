package shop.suite

import cats.effect.IO
import cats.implicits._
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.scalatest.Assertion
import shop.adapter.http.json._

import scala.util.control.NoStackTrace

class HttpTestSuite extends PureTestSuite {

  case object DummyError extends NoStackTrace

  def assertHttp[A: Encoder](routes: HttpRoutes[IO], req: Request[IO])(
      expectedStatus: Status,
      expectedBody: A
  ): IO[Assertion] =
    routes.run(req).value.flatMap {
      case Some(resp) =>
        resp.as[Json].map { json =>
          assert(resp.status.eqv(expectedStatus) && json.dropNullValues.eqv(expectedBody.asJson.dropNullValues))
        }
      case None => fail("route nout found")
    }

  def assertHttpStatus(routes: HttpRoutes[IO], req: Request[IO])(expectedStatus: Status) =
    routes.run(req).value.map {
      case Some(resp) =>
        assert(resp.status.eqv(expectedStatus))
      case None => fail("route nout found")
    }

  def assertHttpFailure(routes: HttpRoutes[IO], req: Request[IO]) =
    routes.run(req).value.attempt.map {
      case Left(_)  => assert(true)
      case Right(_) => fail("expected a failure")
    }

}
