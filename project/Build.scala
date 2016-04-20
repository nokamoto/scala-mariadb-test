import java.util.Properties

import org.flywaydb.sbt.FlywayPlugin.autoImport._
import sbt.Keys._
import sbt._
import scalikejdbc.mapper.SbtPlugin.scalikejdbcSettings

import scala.reflect.io.File

object Build extends sbt.Build {
  val scalikejdbcProperties = {
    val p = new Properties()
    p.load(File("project/scalikejdbc.properties").reader())
    p
  }

  lazy val root = (project in file(".")).settings(scalikejdbcSettings).settings(
    name := "scala-mariadb-test",
    scalaVersion := "2.11.8",
    flywayUrl := scalikejdbcProperties.getProperty("jdbc.url"),
    flywayUser := scalikejdbcProperties.getProperty("jdbc.username"),
    flywayPassword := scalikejdbcProperties.getProperty("jdbc.password"),
    flywaySchemas := scalikejdbcProperties.getProperty("jdbc.schema") :: Nil,
    libraryDependencies ++= Seq(
      "org.flywaydb" % "flyway-core" % buildinfo.BuildInfo.flywayVersion,
      "org.mariadb.jdbc" % "mariadb-java-client" % buildinfo.BuildInfo.mariadbVersion,
      "org.scalikejdbc" %% "scalikejdbc" % scalikejdbc.ScalikejdbcBuildInfo.version,
      "ch.qos.logback" % "logback-classic" % "1.1.3"))
}
