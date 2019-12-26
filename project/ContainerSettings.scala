import com.typesafe.sbt.packager.NativePackagerKeys
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.archetypes.scripts.BatStartScriptKeys
import sbt._

object ContainerSettings extends NativePackagerKeys with BatStartScriptKeys {

  val settings: Def.SettingsDefinition = Seq(
    packageName in Docker := "shopping-cart",
    dockerBaseImage := "openjdk:8u201-jre-alpine3.9",
    dockerExposedPorts ++= Seq(8080),
    dockerUpdateLatest := true,
    makeBatScripts := Seq()
  )

}
