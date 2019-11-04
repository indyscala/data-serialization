val kantanVer = "0.6.0"
val circeVer = "0.12.3"

ThisBuild / organization := "org.indyscala.dataser"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / homepage := Some(url("https://github.com/indyscala/data-serialization"))
ThisBuild / licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

lazy val data = project
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.csv" % kantanVer
      , "com.nrinaudo" %% "kantan.csv-generic" % kantanVer
      , "com.nrinaudo" %% "kantan.csv-java8" % kantanVer
      , "io.circe" %% "circe-parser" % circeVer
      // geodesy dependency for https://en.wikipedia.org/wiki/Vincenty%27s_formulae
      // to calculate distance between lat/lon pair
      , "org.gavaghan" % "geodesy" % "1.1.3"
    )
  )

lazy val circe = project
  .dependsOn(data)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVer
      , "io.circe" %% "circe-generic" % circeVer
      , "io.circe" %% "circe-parser" % circeVer
    )
  )

lazy val protobuf = project
  .dependsOn(data)
  .settings(
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVer
      , "io.circe" %% "circe-generic" % circeVer
      , "io.circe" %% "circe-parser" % circeVer
    )
  )
