import Dependencies._

name := "doobie-db"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= (dependencies ++ testDependencies)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds" // or import scala.language.higherKinds
)