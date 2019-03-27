import sbt._


object Dependencies {
  val circeVersion = "0.10.0"
  val catsVersion = "1.6.0"
  val catsEffectVersion = "1.2.0"
  val elastic4sVersion = "6.2.3"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val catsCore = "org.typelevel" %% "cats-core" % catsVersion
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
  lazy val scribe = "com.outr" %% "scribe" % "2.7.2"
  lazy val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.2" % "provided"
  lazy val macwireAkka = "com.softwaremill.macwire" %% "macrosakka" % "2.3.2" % "provided"
  lazy val macwireutil = "com.softwaremill.macwire" %% "util" % "2.3.2"
  lazy val macwireproxy = "com.softwaremill.macwire" %% "proxy" % "2.3.2"
  lazy val playws = "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.2"
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
  lazy val circeJava8 = "io.circe" %% "circe-java8" % circeVersion
  lazy val circeOptics = "io.circe" %% "circe-optics" % circeVersion
  lazy val circeExtras = "io.circe" %% "circe-generic-extras" % circeVersion
  lazy val scribeSl4f = "com.outr" %% "scribe-slf4j" % "2.7.2"
  lazy val elastic4score = "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
  lazy val elastic4shttp = "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion
  lazy val elastic4sstream = "com.sksamuel.elastic4s" %% "elastic4s-http-streams" % elastic4sVersion
  lazy val elastic4scirce = "com.sksamuel.elastic4s" % "elastic4s-circe_2.12" % elastic4sVersion
}
