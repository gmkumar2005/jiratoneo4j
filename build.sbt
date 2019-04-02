import Dependencies._

fork in runMain := true
fork in run := true
//resolvers +=  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.jiratoneo4j"
ThisBuild / organizationName := "jiratoneo4j"
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

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
    catsCore, catsEffect, pureConfig, circeCore, circeGeneric,
    circeParser, circeJava8, circeOptics, circeExtras,
    elastic4score, elastic4shttp, elastic4sstream, elastic4scirce, blazeServer, http4sCirce, http4sDsl, blazeClient, catsRetry, catsRetryEF
    , logback, pureConfigIo, monocleCore, monocleMacro, monocleLaw, circeIteratee, iterateFiles, fs2Core, fs2IO, circefs2, jawnParser, jawnUtils)
}
lazy val root = (project in file("."))
  .enablePlugins(ShoconPlugin)
  .settings(
    name := "JiraToNeo4j",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= dependencies
  )

