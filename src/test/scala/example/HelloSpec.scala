package example

import cats.effect.internals.{IOAppPlatform, IOContextShift, IOShift}
import cats.effect.{ContextShift, IO}
import io.circe.Json
import jiraimporter._
import org.scalatest._
import io.circe.fs2._
import io.circe.generic.auto._
import java.nio.file.Paths

import io.circe.syntax._
import org.typelevel.jawn.AsyncParser
import fs2.{Stream, io, text}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext


class HelloSpec extends FlatSpec with Matchers {
  def logger = LoggerFactory.getLogger(this.getClass)

  import java.nio.file.Paths
  import java.util.concurrent.Executors


  "The stream decoder" should "decode the jira defect" in {
    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    val blockingEC = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

    val stringStream: Stream[IO, String] =
      io.file.readAll[IO](Paths.get("conf/gppdefect.json"), blockingEC, 4096)
        .through(text.utf8Decode)

    val parsedStream: Stream[IO, Json] = stringStream.through(stringStreamParser)
    val decodedStream: Stream[IO, JiraResponse] = parsedStream.through(decoder[IO, JiraResponse])

    val actualResults = decodedStream.compile.toVector.attempt.unsafeRunSync().right.get.size

    logger.info("actualResults " + actualResults)

  }
}
