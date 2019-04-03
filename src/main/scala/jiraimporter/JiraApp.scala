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
import JiraResponse.JiraIssuesListToBgBug
import JiraResponse.JiraIssuesToBgBug
import java.nio.file.Paths
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object ClientExample extends IOApp with Http4sClientDsl[IO] {
  def logger = LoggerFactory.getLogger(this.getClass)

  val retryFiveTimes = RetryPolicies.limitRetries[IO](5)
  val logMessages = collection.mutable.ArrayBuffer.empty[String]

  def getSite(client: Client[IO]): IO[Unit] = IO {
    import jiraimporter.BGBugDecoder.bgbugdecoder

    val clientWithLoger = Logger[IO](logHeaders = true, logBody = true)(client)
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

    val jiraServerConfig: IO[JiraServer] = loadConfigF[IO, JiraServer]("jira-server")
    implicit val clientWithLoger = Logger[IO](logHeaders = true, logBody = false)(client)
    //
    val totalIssuesOntheServer = findIssueTotal(jiraServerConfig).unsafeRunSync()
    logger.info(s"Total Issues found in jiraServer  : $totalIssuesOntheServer")
    val bugsRange = List.range(0, totalIssuesOntheServer / 100, 500)

    //        val allJiraIssues = bugsRange.map { (pageNo) => getJiraIssues(jiraServerConfig, pageNo) }


    val bugList = bugsRange.map(pageNo => getJiraIssues(jiraServerConfig, pageNo))
    val bulkInsertStatememt = bugList.map((jiraIssue) => putBgBugToES(jiraIssue))
    //
    val commands = bulkInsertStatememt.sequence
    val bulkCommandString = commands.unsafeRunSync

    val fw = new FileWriter("escommands.txt", true)
    bulkCommandString.map(fw.write(_))
    fw.close()

    logger.error("bulkInsertCommands : " + bulkCommandString.size)


    //    val issuesList = results.unsafeRunSync()
    //    logger.error(" Total Issues in the response : " + issuesList.flatten.size)
    //    logger.error(" First Issue : " + issuesList.flatten.take(1))

  }

  def generateESInsert(bgbug: Bgbug): String = {
    val esIndex = Index("jiratest1", "data", bgbug.`Defect ID`.toString)
    val index = ESBulkIndex(esIndex)
    index.asJson.noSpaces + bgbug.asJson.noSpaces
  }

  def generateESInsertBulk(bgbugsList: List[Bgbug]): String = {
    bgbugsList.map(generateESInsert(_)).mkString
  }

  def putBgBugToES(bgbugsList: IO[List[Issues]])(implicit client: Client[IO]): IO[String] = {

    val esBulkInsertCommands = for {
      bgbug <- bgbugsList
      esInsertCommad <- IO {
        generateESInsertBulk(bgbug)
      }
    } yield esInsertCommad

    esBulkInsertCommands


  }

  def getJiraIssues(jiraServerConfig: IO[JiraServer], pageNo: Long)(implicit client: Client[IO]): IO[List[Issues]] = {
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
            .withQueryParam("startAt", pageNo.toString)
            .withQueryParam("maxResults", 50)
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

  def init: Unit = {
    //   val builder = BlazeClientBuilder[IO](global)
    //    builder.withMaxTotalConnections(2)
  }

  val getSSLContext: SSLContext = {
    val permissiveTrustManager: TrustManager = new X509TrustManager() {
      override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

      override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {}

      override def getAcceptedIssuers(): Array[X509Certificate] = Array.empty
    }

    val ctx = SSLContext.getInstance("TLS")
    ctx.init(Array.empty, Array(permissiveTrustManager), new SecureRandom())
    ctx
  }

  def run(args: List[String]): IO[ExitCode] = {
    init
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


