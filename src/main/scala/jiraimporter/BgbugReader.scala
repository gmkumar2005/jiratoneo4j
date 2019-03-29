package jiraimporter

import com.sksamuel.elastic4s.{Hit, HitReader}

object BgbugReader {
  implicit object BgbugReader extends HitReader[Bgbug] {
    override def read(hit: Hit): Either[Throwable, Bgbug] = {
      Right(Bgbug(
//        hit.id,
        hit.sourceAsMap.getOrElse("Detected on Date", "Empty").toString,
        Some(hit.sourceAsMap.getOrElse("Modif_to_Reopen", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Bucket", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Time_Stamp_New", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Affected Fields", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Actual Fix Time", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Closing Date", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Prior_Fixed", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("FRSD Name", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Main Project", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Subject", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Comments", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Original Detected in Project", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Delivery Date", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Detected By", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Close Counter Internal", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Sub Status", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("QA (last)", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Previous Group", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Description", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Technical Component", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Product/Project", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Testing Phase", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("ReTest Failed Counter", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("FRSD Type", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Detected in time", "Empty").toString),
        hit.sourceAsMap.getOrElse("Defect ID", "Empty").asInstanceOf[Int],
        Some(hit.sourceAsMap.getOrElse("View", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Open_date", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Number of 'ReOpen'", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Detected in Project", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Group", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Customer", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Priority", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Is_Old", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Prior Defect Status", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Prevent Copy", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Time_Stamp_Old", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("All Relevant Info Supplied", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Check Field", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Detected in Release", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("NFR  Rejected by Fund", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Last Leg", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Product", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Assigned To", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Summary", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Close Counter External", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Developer (last)", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Severity", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Modified", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Status", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Type", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Raised By", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("Suggested_Resolution", "Empty").toString),
        Some(hit.sourceAsMap.getOrElse("JiraKey", "Empty").toString)
      )
      )
    }
  }
}
