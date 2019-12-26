package shop.infrastructure

import eu.timepit.refined.api._
import eu.timepit.refined.collection.Size

object refined {

  implicit def validateSizeN[N <: Int, R](implicit w: ValueOf[N]): Validate.Plain[R, Size[N]] =
    Validate.fromPredicate[R, Size[N]](
      _.toString.length == w.value,
      _ => s"Must have ${w.value} digits",
      Size[N](w.value)
    )

}
