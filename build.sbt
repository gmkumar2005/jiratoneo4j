/*
 * Copyright 2019 gmkumar2005
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Dependencies._

organizationName := "gmkumar2005"
startYear := Some(2019)
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

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

