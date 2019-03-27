package jiraimporter

import io.circe.{Decoder, HCursor}

object BGBugDecoder {

  implicit val bgbugdecoder: Decoder[Bgbug] = new Decoder[Bgbug] {
    final def apply(c: HCursor): Decoder.Result[Bgbug] =
      for {
        id <- c.downField("id").as[Int]
        //        description <- c.downField("fields").downField("description").as[String]
        summary <- c.downField("fields").downField("summary").as[String]
        mainProject <- c.downField("fields").downField("project").downField("name").as[String]
        //        resolution <- c.downField("fields").downField("resolution").downField("description").as[String]
        priority <- c.downField("fields").downField("priority").downField("name").as[String]
        creator <- c.downField("fields").downField("creator").downField("emailAddress").as[String]
        status <- c.downField("fields").downField("status").downField("name").as[String]
        assignee <- c.downField("fields").downField("assignee").downField("emailAddress").as[String]
        //        components <- c.downField("fields").downField("components").as[String]
        _type <- c.downField("fields").downField("issuetype").downField("name").as[String]
      } yield {
        new Bgbug(id.toString, "", "", "", "", "",
          "", "", "", "", mainProject, "", "",
          "", "", "", "", "", "",
          "", "", "", "",
          "", "", "", "", id, "", "",
          "", "", "", "", priority, "", "",
          "", "", "", "", "", "",
          "", "", assignee, summary, "", "", "", "",
          status, _type, creator, "")
      }
  }
}
