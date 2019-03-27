import Dependencies._
fork in runMain := true
fork in run := true

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.jiratoneo4j"
ThisBuild / organizationName := "jiratoneo4j"

ThisBuild / scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Ypartial-unification")

unmanagedClasspath in Runtime += baseDirectory.value / "conf"

val dependencies = {
  import Dependencies._
  Seq(
    catsCore, catsEffect, scribe, macwire, macwireAkka, macwireutil, macwireproxy, playws, circeCore, circeGeneric, 
    circeParser,scribeSl4f,circeJava8,circeOptics,circeExtras,
    elastic4score,elastic4shttp,elastic4sstream,elastic4scirce
  )
}
lazy val root = (project in file("."))
  .enablePlugins(ShoconPlugin)
  .settings(
    name := "JiraToNeo4j",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= dependencies
  )

