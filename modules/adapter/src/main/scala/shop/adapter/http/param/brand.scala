package shop.adapter.http.param

import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import shop.domain.Brands.BrandName

object brand {

  @newtype case class BrandParam(value: NonEmptyString) {
    def toDomain: BrandName = BrandName(value.value.toLowerCase.capitalize)
  }

}
