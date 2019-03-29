package jiraimporter

import JsonSerializers._

object BgbugSummary {
  implicit val jsonEncoder: Encoder[BgbugSummary] = deriveEncoder
  implicit val jsonDecoder: Decoder[BgbugSummary] = deriveDecoder
}

case class BgbugSummary(totalHits: Long, seq: Seq[Bgbug], _id: String)

object Bgbug {
  implicit val jsonEncoder: Encoder[Bgbug] = deriveEncoder
  implicit val jsonDecoder: Decoder[Bgbug] = deriveDecoder
}

case class Bgbug(
                  //    `_id`: String,
                  `Detected on Date`: String,
                  `Modif_to_Reopen`: Option[String],
                  `Bucket`: Option[String],
                  `Time_Stamp_New`: Option[String],
                  `Affected Fields`: Option[String],
                  `Actual Fix Time`: Option[String],
                  `Closing Date`: Option[String],
                  `Prior_Fixed`: Option[String],
                  `FRSD Name`: Option[String],
                  `Main Project`: Option[String],
                  `Subject`: Option[String],
                  `Comments`: Option[String],
                  `Original Detected in Project`: Option[String],
                  `Delivery Date`: Option[String],
                  `Detected By`: Option[String],
                  `Close Counter Internal`: Option[String],
                  `Sub Status`: Option[String],
                  `QA (last)`: Option[String],
                  `Previous Group`: Option[String],
                  `Description`: Option[String],
                  `Technical Component`: Option[String],
                  `Product/Project`: Option[String],
                  `Testing Phase`: Option[String],
                  `ReTest Failed Counter`: Option[String],
                  `FRSD Type`: Option[String],
                  `Detected in time`: Option[String],
                  `Defect ID`: Int,
                  `View`: Option[String],
                  `Open_date`: Option[String],
                  `Number of 'ReOpen'`: Option[String],
                  `Detected in Project`: Option[String],
                  `Group`: Option[String],
                  `Customer`: Option[String],
                  `Priority`: Option[String],
                  `Is_Old`: Option[String],
                  `Prior Defect Status`: Option[String],
                  `Prevent Copy`: Option[String],
                  `Time_Stamp_Old`: Option[String],
                  `All Relevant Info Supplied`: Option[String],
                  `Check Field`: Option[String],
                  `Detected in Release`: Option[String],
                  `NFR  Rejected by Fund`: Option[String],
                  `Last Leg`: Option[String],
                  `Product`: Option[String],
                  `Assigned To`: Option[String],
                  `Summary`: Option[String],
                  `Close Counter External`: Option[String],
                  `Developer (last)`: Option[String],
                  `Severity`: Option[String],
                  `Modified`: Option[String],
                  `Status`: Option[String],
                  `Type`: Option[String],
                  `Raised By`: Option[String],
                  Suggested_Resolution: Option[String],
                  `JiraKey`: Option[String],
                )

object SearchResult {
  implicit val jsonEncoder: Encoder[SearchResult] = deriveEncoder
  implicit val jsonDecoder: Decoder[SearchResult] = deriveDecoder
}

case class SearchResult(totalHits: Long, bgBugs: Seq[Bgbug])

object ESQuery {
  implicit val jsonEncoder: Encoder[ESQuery] = deriveEncoder
  implicit val jsonDecoder: Decoder[ESQuery] = deriveDecoder
}

case class ESQuery(query: String, start: Int, limit: Int)
