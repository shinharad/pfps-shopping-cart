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

  case class Category(uuid: CategoryId, name: CategoryName)

}
