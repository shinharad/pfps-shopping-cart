package suite

import java.util.UUID

import cats.effect.{ ContextShift, IO, Timer }
import org.scalactic.source.Position
import org.scalatest.compatible.Assertion
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import scala.concurrent.ExecutionContext

trait PureTestSuite extends AsyncFunSuite with ScalaCheckDrivenPropertyChecks {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO]     = IO.timer(ExecutionContext.global)

  private def mkUnique(name: String): String =
    s"$name - ${UUID.randomUUID}"

  def spec(testName: String)(f: => IO[Assertion])(implicit pos: Position): Unit =
    test(mkUnique(testName))(IO.suspend(f).unsafeToFuture())

}
