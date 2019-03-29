package jiraimporter

import akka.actor.{ActorSystem, Terminated}
import akka.stream.ActorMaterializer
import cats.data.EitherT
import cats.instances.future.catsStdInstancesForFuture
import com.sksamuel.elastic4s.ElasticsearchClientUri
import scribe.writer.FileWriter
import scribe.writer.file.LogPath
//import com.sksamuel.elastic4s.http.ElasticDsl._
//import com.sksamuel.elastic4s.ElasticApi._
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess, SourceAsContentBuilder, get => _, search => _}
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.Json
import io.circe.optics.JsonPath.root
import io.circe.parser.parse
import jiraimporter.BGBugDecoder.bgbugdecoder
import play.api.libs.ws._
import play.api.libs.ws.ahc._
import scribe._
import scribe.format._
import com.sksamuel.elastic4s.http.ElasticDsl._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.extras.Configuration

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.duration._

object JiraApp extends Logging {

  implicit val system: ActorSystem = ActorSystem()
  val config: Config = ConfigFactory.load()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val wsClient = StandaloneAhcWSClient()
  implicit val configuration: Configuration = Configuration.default.withDefaults

  private val esIndex = config.getString("dhiNidhi.index")
  private val elasticServer = config.getString("dhiNidhi.elasticServer")
  private val elasticServerPort = config.getInt("dhiNidhi.elasticServerPort")
  private val client = HttpClient(ElasticsearchClientUri(elasticServer, elasticServerPort))


  system.registerOnTermination {
    System.exit(0)
  }


  def main(args: Array[String]): Unit = {
    init

    val processJiraImport = for {
      bglist <- processJiraRequest
      esList <- EitherT(insertIntoEs(bglist))
    } yield esList

    processJiraImport.value.onComplete(result => {
      logger.debug("Inserted " + result + " documents ")
      shutdown(system, wsClient)
    })
  }


  def insertIntoEs(bgbugs: List[Bgbug]) = {
  logger.info("Inserting into ESIndex " + esIndex)
    val insertOps = bgbugs.map({ (bgbug) => indexInto(esIndex).doc(bgbug.asJson.noSpaces).withId(bgbug.`Defect ID`.toString) } )
//    val insertOps = bgbugs.map({ (bgbug) => update(bgbug.`Defect ID`.toString).in("jiratest1" / "data").docAsUpsert(bgbug.asJson.noSpaces) } ).take(2)
    client.execute(bulk(insertOps: _*)).map {
      case Left(err) => Left[Error, String](Error.EsServerNoReachable("Error inserting document :" + elasticServer + "Message :" + err))
      case Right(i) => Right[Error, String](i.result.items.toString())
    }

  }

  def init: Unit = {
//    val myFormatter: Formatter = formatter"[$threadName] $positionAbbreviated - $message$newLine"
//    Logger.root.withHandler(formatter = myFormatter).replace()
//    Logger("com.sksamuel.elastic4s").clearHandlers().clearModifiers().withHandler(minimumLevel = Some(Level.Debug)).replace()
//    Logger().clearHandlers().clearModifiers().withHandler(minimumLevel = Some(Level.Debug)).replace()
    Logger().clearHandlers().clearModifiers().withHandler(minimumLevel = Some(Level.Debug)).withHandler(writer = FileWriter().path(LogPath.daily())).replace()
    logger.info("Starting ..")
  }

  def runEs: EitherT[Future, Error, SearchResult] = {
    val esAttempt = for {
      esResponse <- EitherT(processEsRequests)
    } yield esResponse
    esAttempt.value.onComplete(result => {
      logger.info(" ES Response processing ..")
      val bgbugsCount = result.map({ l => l.map({ r => (r.bgBugs.size, r.totalHits) }) })
      logger.debug("BgBugs :" + result)
      logger.info("BgBugs found in elastic server " + bgbugsCount)

      //      shutdown(system, wsClient)
    })

    esAttempt
  }


  def processEsRequests: Future[Either[Error, SearchResult]] = {
    import BgbugReader.BgbugReader
    val limit = 2

    val query = search(esIndex) query "defect" limit {
      limit
    }
    val resp = client.execute(query).map {
      case Left(err) => Left[Error, SearchResult](Error.EsServerNoReachable("dhiNidhi.elasticServer :" + elasticServer + "Message :" + err))
      case Right(i) => Right[Error, SearchResult](SearchResult(i.result.totalHits, i.result.to[Bgbug]))

    }

    resp
  }

  def processJiraRequest: EitherT[Future, Error, List[Bgbug]] = {
    val jiraClient = fetchJiraIssues(wsClient) _
    val attempt = for {
      jiraresponse <- EitherT(jiraClient(config.getString("jiraServer.baseUrl"),
        config.getString("jiraServer.jiraUserName"),
        config.getString("jiraServer.jiraPassword")))
      jiraJson <- EitherT(getIssuesFromJiraResponse(jiraresponse))
      bgbugs <- EitherT(getBgBugs(jiraJson))
    } yield bgbugs

    attempt.value.onComplete(result => {

      val convertedCount = result.map(l => l.map({ li => li.size }))

      logger.info("Total bgbugs converted " + convertedCount)
      logger.debug("Total bgbugs converted " + result)

    })
    attempt

  }

  def fetchJiraIssues(wsClient: StandaloneWSClient)(jiraBaseUrl: String, jiraUserName: String, jiraPassword: String): Future[Either[Error, String]] = {
    val request: StandaloneWSRequest = wsClient.url(jiraBaseUrl + "/search").withAuth(jiraUserName, jiraPassword, WSAuthScheme.BASIC)
    val complexRequest: StandaloneWSRequest =
      request.addHttpHeaders("Accept" -> "application/json")
        .addHttpHeaders("Accept-Encoding" -> "gzip,deflate")
        .addQueryStringParameters("jql" -> "project = FCS AND updated >= -1d")
        .addQueryStringParameters("startAt" -> "0")
        .addQueryStringParameters("maxResults" -> "500")
//      .addQueryStringParameters(startAt=3&maxResults=5")
        .withRequestFilter(AhcCurlRequestLogger())
        .withRequestTimeout(90000.millis)

    complexRequest.get().map(resp => resp.status match {
      case 200 =>
        logger.info("Recieving data from the jiraServer ...")
        Right(resp.body)

      case _ =>
        logger.info("Unknown error resp.status " + resp.status)
        Left(Error.JiraServerNoReachable("jiraBaseUrl :" + jiraBaseUrl + " Response : " + resp.body))

    })
  }


  def getIssuesFromJiraResponse(result: String): Future[Either[Error, Json]] = {
    Future {
      parse(result) match {
        case Right(value) => Right(value)
        case Left(error) => Left(Error.JiraIssuesParsing(error.message))
      }
    }
  }

  def getBgBugs(jiraJson: Json): Future[Either[Error, List[Bgbug]]] = {
    val _total = root.total.int
    val _issues = root.issues.each.json
    val totalIssuesFound = _total.getOption(jiraJson)
    logger.info("Total issues found " + totalIssuesFound.getOrElse(0))
    Future {
      val decodedBGBugs = _issues.getAll(jiraJson).map(x => x.as[Bgbug])
      val validBgbugs = decodedBGBugs collect { case Right(f) => f }
      val invalidBgBugs = decodedBGBugs collect { case Left(f) => f }
      if (validBgbugs.size <= 0) {
        Left[Error, List[Bgbug]](Error.JiraIssuesParsing("Zero issues converted " + _issues.getAll(jiraJson).map(x => x.as[Bgbug])))
      } else if (validBgbugs.size < totalIssuesFound.getOrElse(0)) {
        val bgsize = validBgbugs.size
        logger.warn(s"Total converted issue $bgsize is less than TotalIssuesFound $totalIssuesFound")
        logger.warn(s"Decoding failed issues \n" + invalidBgBugs)
        Right[Error, List[Bgbug]](validBgbugs)
      }
      else {
        Right[Error, List[Bgbug]](validBgbugs)
      }
    }

  }

  def shutdown(system: ActorSystem, wsClient: StandaloneAhcWSClient): Future[Terminated] = {
    logger.info("Shutdown initiated ...")
    wsClient.close()
    system.terminate()
  }

}

sealed trait Error

object Error {

  final case class JiraServerNoReachable(baseUrl: String) extends Error

  final case class EsServerNoReachable(esUrl: String) extends Error

  final case class JiraIssuesParsing(msg: String) extends Error

}