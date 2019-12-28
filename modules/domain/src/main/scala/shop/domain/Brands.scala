package shop.domain

import java.util.UUID

import io.estatico.newtype.macros.newtype
import shop.domain.Brands._

object Brands {

  @newtype case class BrandId(value: UUID)
  @newtype case class BrandName(value: String)

  case class Brand(id: BrandId, name: BrandName)

  // TODO これは定義するところが違う気がする
//  @newtype case class BrandParam(value: NonEmptyString) {
//    def toDomain: BrandName = BrandName(value.value.toLowerCase.capitalize)
//  }

}

trait Brands[F[_]] {
  def findAll: F[List[Brand]]
  def create(name: BrandName): F[Unit]
}
