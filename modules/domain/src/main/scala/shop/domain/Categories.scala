package shop.domain

import java.util.UUID

import io.estatico.newtype.macros.newtype

trait Categories[F[_]] {
  import Categories._
  def findAll: F[List[Category]]
  def create(name: CategoryName): F[Unit]
}

object Categories {

  @newtype case class CategoryId(value: UUID)
  @newtype case class CategoryName(value: String)

//  @newtype case class CategoryParam(value: NonEmptyString) {
//    def toDomain: CategoryName = CategoryName(value.value.toLowerCase.capitalize)
//  }

  case class Category(uuid: CategoryId, name: CategoryName)

}
