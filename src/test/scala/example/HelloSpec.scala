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
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext


class HelloSpec extends FlatSpec with Matchers {
  def logger = LoggerFactory.getLogger(this.getClass)

  import java.nio.file.Paths
  import java.util.concurrent.Executors
  import JiraResponse.jiraIssuesToBgBug
  import JiraResponse.jiraIssuesListToBgBug
  import scala.language.implicitConversions

  "The stream decoder" should "decode the jira defect" in {
    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    val blockingEC = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

    val stringStream: Stream[IO, String] =
      io.file.readAll[IO](Paths.get("conf/gppdefect.json"), blockingEC, 4096)
        .through(text.utf8Decode)
    val parsedStream: Stream[IO, Json] = stringStream.through(stringStreamParser)
    val decodedStream: Stream[IO, JiraResponse] = parsedStream.through(decoder[IO, JiraResponse])


    val actualResults = decodedStream.compile.toVector.attempt.unsafeRunSync().right.get(0).issues



    //    logger.info("actualResults " + actualResults)
//    logger.info("Convert JiraIssuesListToBgBug " + JiraIssuesListToBgBug(actualResults))
  }
  "The bulk generator" should " generate valid bulk insert string" in {
    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    val blockingEC = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

    val stringStream: Stream[IO, String] =
      io.file.readAll[IO](Paths.get("conf/gppdefect.json"), blockingEC, 4096)
        .through(text.utf8Decode)

    val parsedStream: Stream[IO, Json] = stringStream.through(stringStreamParser)
    val decodedStream: Stream[IO, JiraResponse] = parsedStream.through(decoder[IO, JiraResponse])


    val actualResults = decodedStream.compile.toVector.attempt.unsafeRunSync().right.get(0).issues
//    logger.info("Single insert request" + ClientExample.generateESInsert(actualResults.head))
    logger.info("bulk insert request" + ClientExample.generateESInsertBulk(actualResults))
    logger.info("Long data " + 123132L.asJson.spaces2)
    val responseString = """{"took":24,"errors":true,"items":[{"index":{"_index":"test","_type":"_doc","_id":"1","status":409,"error":{"type":"version_conflict_engine_exception","reason":"[_doc][3]: version conflict, PAXOS insert failed, document already exists","index_uuid":"uC_LjOGVTSWGo-w4IXOoaQ","shard":"0","index":"test"}}},{"delete":{"_index":"test","_type":"_doc","_id":"2","status":409,"error":{"type":"version_conflict_engine_exception","reason":"[_doc][3]: version conflict, PAXOS insert failed, document already exists","index_uuid":"uC_LjOGVTSWGo-w4IXOoaQ","shard":"0","index":"test"}}},{"create":{"_index":"test","_type":"_doc","_id":"3","status":409,"error":{"type":"version_conflict_engine_exception","reason":"[_doc][3]: version conflict, PAXOS insert failed, document already exists","index_uuid":"uC_LjOGVTSWGo-w4IXOoaQ","shard":"0","index":"test"}}},{"update":{"_index":"test","_type":"_doc","_id":"1","status":409,"error":{"type":"version_conflict_engine_exception","reason":"[_doc][3]: version conflict, PAXOS insert failed, document already exists","index_uuid":"uC_LjOGVTSWGo-w4IXOoaQ","shard":"0","index":"test"}}}]}"""

    logger.info(" Decoded " + responseString.asJson)
  }
}
