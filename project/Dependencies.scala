import sbt._

object Dependencies {
  lazy val akkaParent = "com.typesafe.akka"
  lazy val akkaVersion = "2.5.23"
  lazy val monixParent = "io.monix"
  lazy val monixVersion = "3.1.0"
  lazy val doobieParent = "org.tpolecat"
  lazy val doobieVersion = "0.8.6"
  lazy val typeLevelParent = "org.typelevel"
  lazy val catsVersion = "2.0.0"
  lazy val scalaLoggingParent = "com.typesafe.scala-logging"
  lazy val scalaLoggingVersion = "3.9.2"
  lazy val logbackParent = "ch.qos.logback"
  lazy val logbackVersion = "1.2.3"
  lazy val logbackEncoderParent = "net.logstash.logback"
  lazy val logbackEncoderVersion = "6.3"
  lazy val scalaTestParent = "org.scalatest"
  lazy val scalaTestVersion = "3.0.8"
  lazy val scalaMockParent = "org.scalamock"
  lazy val scalaMockVersion = "4.4.0"

  val dependencies = Seq(
    akkaParent %% "akka-actor" % akkaVersion,
    monixParent %% "monix-reactive" % monixVersion,
    doobieParent %% "doobie-core" % doobieVersion,
    doobieParent %% "doobie-hikari" % doobieVersion,
    doobieParent %% "doobie-postgres" % doobieVersion,
    doobieParent %% "doobie-quill" % doobieVersion,
    typeLevelParent %% "cats-macros" % catsVersion,
    typeLevelParent %% "cats-kernel" % catsVersion,
    typeLevelParent %% "cats-core" % catsVersion,
    typeLevelParent %% "cats-effect" % catsVersion,
    scalaLoggingParent %% "scala-logging" % scalaLoggingVersion,
    logbackParent % "logback-classic" % logbackVersion,
    logbackEncoderParent % "logstash-logback-encoder" % logbackEncoderVersion
  )

  val testDependencies = Seq(
    scalaTestParent %% "scalatest" % scalaTestVersion % Test,
    scalaMockParent %% "scalamock" % scalaMockVersion % Test,
    doobieParent %% "doobie-specs2"    % doobieVersion % Test,
    doobieParent %% "doobie-scalatest" % doobieVersion % Test
  )
}
