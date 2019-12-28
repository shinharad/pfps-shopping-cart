package shop.domain

import java.util.UUID

import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype

trait Brands[F[_]] {
  import Brands._
  def findAll: F[List[Brand]]
  def create(name: BrandName): F[Unit]
}

object Brands {

  @newtype case class BrandId(value: UUID)
  @newtype case class BrandName(value: String)

  @newtype case class BrandParam(value: NonEmptyString) {
    def toDomain: BrandName = BrandName(value.value.toLowerCase.capitalize)
  }

  case class Brand(id: BrandId, name: BrandName)

}
