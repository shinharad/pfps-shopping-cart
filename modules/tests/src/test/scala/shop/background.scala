package shop

import cats.effect.IO
import cats.effect.concurrent.Ref
import shop.infrastructure.effect.Background

import scala.concurrent.duration.FiniteDuration

object background {

  val NoOp: Background[IO] = new Background[IO] {
    def schedule[A](fa: IO[A], duration: FiniteDuration): IO[Unit] = IO.unit
  }

  def counter(ref: Ref[IO, Int]): Background[IO] =
    new Background[IO] {
      def schedule[A](fa: IO[A], duration: FiniteDuration): IO[Unit] =
        ref.update(_ + 1)
    }

}
