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

package jiraimporter

import java.io.{File, FileOutputStream, FileWriter}
import java.security.SecureRandom
import java.security.cert.X509Certificate

import retry._
import cats.data.EitherT
import cats.effect._
import cats.implicits._
import fs2.Stream
import io.circe.Json
import io.circe.generic.auto._
import javax.net.ssl.{SSLContext, TrustManager, X509TrustManager}
import jiraimporter.ClientExample.getJiraIssues
import org.http4s.{AuthScheme, BasicCredentials, Credentials, Header, Headers, HttpVersion, Method, Request, Uri}
import org.http4s.Status.{NotFound, Successful}
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.blaze.{BlazeClientBuilder, BlazeClientConfig}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.dsl.io._
import org.http4s.client.middleware.{Logger, RequestLogger}
import org.http4s.server.middleware.authentication.BasicAuth
import org.joda.time.Minutes
import org.slf4j.LoggerFactory
// import org.http4s.client.dsl.io._

import org.http4s.headers._
// import org.http4s.headers._

import org.http4s.MediaType
// import org.http4s.MediaType

import scala.concurrent.ExecutionContext.global
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect._
import cats.effect.IO
import pureconfig.error.ConfigReaderFailures
import pureconfig.loadConfig
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect._
import cats.effect.IO
import org.http4s.Uri
import cats._, cats.effect._, cats.implicits._, cats.data._
import org.http4s.client.dsl.io._
import org.http4s.client.dsl.Http4sClientDsl._
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import io.circe.optics.JsonPath.root
import io.circe.parser.parse
import jiraimporter.BGBugDecoder.bgbugdecoder
import io.circe.Decoder, io.circe.generic.auto._
import org.http4s.circe.CirceEntityDecoder._
import cats.implicits._
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import retry.RetryDetails._
import cats.effect.Timer
import retry.CatsEffect._
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import scala.language.implicitConversions
import java.security.KeyStore
import JiraResponse.jiraIssuesListToBgBug
import JiraResponse.jiraIssuesToBgBug
import java.nio.file.Paths
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object ClientExample extends IOApp with Http4sClientDsl[IO] {
  val logger = LoggerFactory.getLogger(this.getClass)

  val MAX_RETRIES = 5
  val retryFiveTimes = RetryPolicies.limitRetries[IO](MAX_RETRIES)
  val logMessages = collection.mutable.ArrayBuffer.empty[String]
  implicit val jiraServerConfig: IO[JiraServer] = loadConfigF[IO, JiraServer]("jira-server")
  implicit val printer = Printer(
    preserveOrder = true,
    dropNullValues = true,
    indent = " ",
    lbraceRight = " ",
    rbraceLeft = " ",
    lbracketRight = "\n",
    rbracketLeft = "\n",
    lrbracketsEmpty = "\n",
    arrayCommaRight = "\n",
    objectCommaRight = " ",
    colonLeft = " ",
    colonRight = " "
  )
  val _maxResults = 500

  def getSite(client: Client[IO]): IO[Unit] = IO {
    import jiraimporter.BGBugDecoder.bgbugdecoder

    val clientWithLoger = Logger[IO](logHeaders = true, logBody = false)(client)
    val jiraServerConfig: IO[JiraServer] = loadConfigF[IO, JiraServer]("jira-server")

    val req3: Request[IO] = Request()

    val data = for {
      jc <- jiraServerConfig
      page2 <- {
        clientWithLoger.expect[JiraResponse](
          req3
            .withHeaders(Headers(Accept(MediaType.application.json), Authorization(BasicCredentials(jc.jiraUserName, jc.jiraPassword.value))))
            .withMethod(Method.GET).withUri(Uri.fromString(jc.baseUrl + "/search").valueOr(throw _)
            .withQueryParam("jql", "project=" + jc.jql)
            .withQueryParam("startAt", 0)
            .withQueryParam("maxResults", 1)
          )
        )
      }
    } yield page2


    val totalJiraIssuesFound = data.unsafeRunSync().total

    logger.error(" Total Issues found : " + totalJiraIssuesFound.toString)
    //    logger.info("Second Call " + data.unsafeRunSync().take(100))
  }


  def getSite2(client: Client[IO]): IO[Unit] = IO {
    logger.info("Starting IO")


    implicit val clientWithLoger = Logger[IO](logHeaders = true, logBody = false)(client)
    //
    val totalIssuesOntheServer = findIssueTotal(jiraServerConfig).unsafeRunSync()
    logger.info(s"Total Issues found in jiraServer  : $totalIssuesOntheServer")
    val bugsRange = List.range(0, totalIssuesOntheServer, _maxResults)

    //        val allJiraIssues = bugsRange.map { (pageNo) => getJiraIssues(jiraServerConfig, pageNo) }


    val bugList = bugsRange.map(pageNo => getJiraIssues(pageNo))
    val bulkInsertStatememt = bugList.map(jiraIssue => getBgBugToESCommands(jiraIssue))
    //
    val esbulkCommands = bulkInsertStatememt.map(command => runESBulkCommands(command))
    val esinsertResults = esbulkCommands.sequence.unsafeRunSync

    //    val bulkCommandString = bulkInsertStatememt.sequence.unsafeRunSync
    //
    //    val fw = new FileWriter("escommands.txt", true)
    //    bulkCommandString.map(fw.write(_))
    //
    //    fw.close()

    logger.error("Total items inserted in BULK  : " + esinsertResults.sum)


    //    val issuesList = results.unsafeRunSync()
    //    logger.error(" Total Issues in the response : " + issuesList.flatten.size)
    //    logger.error(" First Issue : " + issuesList.flatten.take(1))

  }

  def runESBulkCommands(command: IO[String])(implicit client: Client[IO]): IO[Int] = {
    def defaultHeader: Header = Header("Content-Type", "application/json")

    for {
      cmd <- command
      esResponse <- client.expect[ESResponse](
        Request[IO]()
          //          .withHeaders(Headers(defaultHeader))
          .withHeaders(Headers(Accept(MediaType.text.plain)))
          .withMethod(Method.POST)
          .withUri(Uri.fromString("http://10.91.10.13:9200/_bulk").valueOr(throw _)
          ).withEntity(cmd).withHeaders(defaultHeader, Accept(MediaType.text.plain))
      )
    } yield esResponse.items.size

  }

  def generateESInsert(bgbug: Bgbug): String = {
    val esIndex = Index("jiratest1", "data", bgbug.`Defect ID`)
    val index = ESBulkIndex(esIndex)
    index.asJson.pretty(printer) + "\n" + bgbug.asJson.pretty(printer) + " \n"
  }

  def generateESInsertBulk(bgbugsList: List[Bgbug]): String = {
    bgbugsList.map(generateESInsert).mkString
  }

  def getBgBugToESCommands(bgbugsList: IO[List[Issues]])(implicit client: Client[IO]): IO[String] = {

    val esBulkInsertCommands = for {
      bgbug <- bgbugsList
      esInsertCommad <- IO {
        generateESInsertBulk(bgbug)
      }
    } yield esInsertCommad

    esBulkInsertCommands


  }

  def getJiraIssues(pageNo: Long)(implicit jiraServerConfig: IO[JiraServer], client: Client[IO]): IO[List[Issues]] = {
    //    Thread.sleep(500)
    logger.info("Processing pageNo :" + pageNo)
    //    val acceptHeader = Header("Accept-Encoding", "gzip")
    val data = for {
      jc <- jiraServerConfig
      page2 <- {
        client.expect[JiraResponse](
          Request[IO]()
            .withHeaders(Headers(Accept(MediaType.application.json), Authorization(BasicCredentials(jc.jiraUserName, jc.jiraPassword.value))))
            .withMethod(Method.GET).withUri(Uri.fromString(jc.baseUrl + "/search").valueOr(throw _)
            .withQueryParam("jql", jc.jql)
            .withQueryParam("startAt", pageNo)
            .withQueryParam("maxResults", _maxResults)
          )
        )
      }
    } yield page2.issues

    val flakyRequestWithRetry: IO[List[Issues]] =
      retryingOnAllErrors[List[Issues]](
        policy = retryFiveTimes,
        onError = logError
      )(data)

    flakyRequestWithRetry
  }

  val getSSLContext: SSLContext = {
    val permissiveTrustManager: TrustManager = new X509TrustManager() {
      override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

      override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

      override def getAcceptedIssuers: Array[X509Certificate] = Array.empty
    }

    val ctx = SSLContext.getInstance("TLS")
    ctx.init(Array.empty, Array(permissiveTrustManager), new SecureRandom())
    ctx
  }

  def run(args: List[String]): IO[ExitCode] = {
    BlazeClientBuilder[IO](global)
      .withSslContext(getSSLContext)
      //      .withMaxTotalConnections(2)
      .withRequestTimeout(5.minutes)
      .resource
      .use(getSite2)
      .as(ExitCode.Success)
  }

  def findIssueTotal(jiraServerConfig: IO[JiraServer])(implicit client: Client[IO]): IO[Long] = {
    val data = for {
      jc <- jiraServerConfig
      page2 <- {
        client.expect[JiraResponse](
          Request[IO]()
            .withHeaders(Headers(Accept(MediaType.application.json), Authorization(BasicCredentials(jc.jiraUserName, jc.jiraPassword.value))))
            .withMethod(Method.GET).withUri(Uri.fromString(jc.baseUrl + "/search").valueOr(throw _)
            .withQueryParam("jql", jc.jql)
            .withQueryParam("startAt", 0)
            .withQueryParam("maxResults", 1)
          )
        )
      }
    } yield page2.total
    data
  }

  def logError(err: Throwable, details: RetryDetails): IO[Unit] = details match {

    case WillDelayAndRetry(nextDelay: FiniteDuration,
    retriesSoFar: Int,
    cumulativeDelay: FiniteDuration) =>
      IO {
        logMessages.append(
          s"Failed to download. So far we have retried $retriesSoFar times.")
      }

    case GivingUp(totalRetries: Int, totalDelay: FiniteDuration) =>
      IO {
        logMessages.append(s"Giving up after $totalRetries retries")
      }

  }

}

case class Sensitive(value: String) extends AnyVal {
  override def toString: String = "MASKED"
}

case class JiraServer(baseUrl: String, jiraUserName: String, jiraPassword: Sensitive, jql: String)


