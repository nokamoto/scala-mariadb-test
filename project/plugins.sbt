enablePlugins(BuildInfoPlugin)

val flywayVersion = "4.0"

val mariadbVersion = "1.1.7"

buildInfoPackage := "buildinfo"

buildInfoKeys := Seq[BuildInfoKey]("flywayVersion" -> flywayVersion, "mariadbVersion" -> mariadbVersion)

addSbtPlugin("org.flywaydb" % "flyway-sbt" % flywayVersion)

resolvers += "Flyway" at "https://flywaydb.org/repo"

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.3.5")

libraryDependencies += "org.mariadb.jdbc" % "mariadb-java-client" % mariadbVersion
