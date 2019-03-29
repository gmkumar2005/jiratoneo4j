package jiraimporter

import io.circe.{Decoder, HCursor}

import scala.util.Success

object BGBugDecoder {

  implicit val bgbugdecoder: Decoder[Bgbug] = new Decoder[Bgbug] {
    final def apply(c: HCursor): Decoder.Result[Bgbug] =
      for {
        id <- c.downField("id").as[Int]
        key <- c.downField("key").as[Option[String]]
        //        description <- c.downField("fields").downField("description").as[String]
        summary <- c.downField("fields").downField("summary").as[Option[String]]
        mainProject <- c.downField("fields").downField("project").downField("name").as[Option[String]]
        //        resolution <- c.downField("fields").downField("resolution").downField("description").as[String]
        priority <- c.downField("fields").downField("priority").downField("name").as[Option[String]]
        _raisedBy <- if (c.downField("fields").downField("creator").downField("emailAddress").succeeded)
          c.downField("fields").downField("creator").downField("emailAddress").as[Option[String]] else {
          Right(None)
        }
        status <- c.downField("fields").downField("status").downField("name").as[Option[String]]
        _sub_status <- c.downField("fields").downField("status").downField("name").as[Option[String]]
        assignee <- if (c.downField("fields").downField("assignee").downField("emailAddress").succeeded)
          c.downField("fields").downField("assignee").downField("emailAddress").as[Option[String]] else {
          Right(None)
        }
        //        components <- c.downField("fields").downField("components").as[String]
        _type <- c.downField("fields").downField("issuetype").downField("name").as[Option[String]]
        _detectedondate <- c.downField("fields").downField("created").as[String]
        _description <- c.downField("fields").downField("description").as[Option[String]]
        _project <- c.downField("fields").downField("project").downField("name").as[Option[String]]
        _updated <- if (c.downField("fields").downField("updated").succeeded) c.downField("fields").downField("updated").as[Option[String]] else {Right(None)}
      } yield {
        new Bgbug(_detectedondate, None, None, None, None,
          None, None, None, None, mainProject, None, None,
          None, None, None, None, _sub_status, None,
          None, _description, None, None,
          None, None, None, None, id, None, None,
          None, None, None, None, priority, None, None,
          None, None, None, None, None, None,
          None, _project, assignee, summary, None, None, None, _updated,
          status, _type, _raisedBy, None,key)
      }
  }
}
