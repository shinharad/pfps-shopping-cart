package shop.domain

import java.util.UUID

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string._
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import shop.domain.Brands._
import shop.domain.Categories._
import squants.market._

trait Items[F[_]] {
  import Items._
  def findAll: F[List[Item]]
  def findBy(brand: BrandName): F[List[Item]]
  def findById(itemId: ItemId): F[Option[Item]]
  def create(item: CreateItem): F[Unit]
  def update(item: UpdateItem): F[Unit]
}

object Items {

  @newtype case class ItemId(value: UUID)
  @newtype case class ItemName(value: String)
  @newtype case class ItemDescription(value: String)

  case class Item(
      uuid: ItemId,
      name: ItemName,
      description: ItemDescription,
      price: Money,
      brand: Brand,
      category: Category
  )

  // ----- Create item ------
  @newtype case class ItemNameParam(value: NonEmptyString)
  @newtype case class ItemDescriptionParam(value: NonEmptyString)

  case class CreateItemParam(
      name: ItemNameParam,
      description: ItemDescriptionParam,
      price: Money,
      brandId: BrandId,
      categoryId: CategoryId
  ) {
    def toDomain: CreateItem =
      CreateItem(
        ItemName(name.value.value),
        ItemDescription(description.value.value),
        price,
        brandId,
        categoryId
      )
  }

  case class CreateItem(
      name: ItemName,
      description: ItemDescription,
      price: Money,
      brandId: BrandId,
      categoryId: CategoryId
  )

  // ----- Update item ------
  @newtype case class ItemIdParam(value: String Refined Uuid)
  @newtype case class PriceParam(value: String Refined ValidBigDecimal)

  case class UpdateItemParam(
      id: ItemIdParam,
      price: PriceParam
  ) {
    def toDomain: UpdateItem =
      UpdateItem(
        ItemId(UUID.fromString(id.value.value)),
        USD(BigDecimal(price.value.value))
      )
  }

  case class UpdateItem(
      id: ItemId,
      price: Money
  )

}
