package shop.adapter.http.request

import java.util.UUID

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string._
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import shop.domain.Brands.BrandId
import shop.domain.Categories.CategoryId
import shop.domain.Items._
import squants.market._

object items {

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
