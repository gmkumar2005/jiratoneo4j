
package jiraimporter

import JsonSerializers._
import scala.language.implicitConversions

case class Customfield_13062(
                              self: Option[String],
                              value: String,
                              id: String
                            )

case class FixVersions(
                        self: Option[String],
                        id: String,
                        description: Option[String],
                        name: String,
                        archived: Option[Boolean],
                        released: Option[Boolean]
                      )

case class Resolution(
                       self: Option[String],
                       id: String,
                       description: Option[String],
                       name: String
                     )

case class AvatarUrls(
                       `48x48`: Option[String],
                       `24x24`: Option[String],
                       `16x16`: Option[String],
                       `32x32`: Option[String]
                     )

case class Customfield_13502(
                              self: Option[String],
                              name: String,
                              key: String,
                              emailAddress: Option[String],
                              avatarUrls: Option[AvatarUrls],
                              displayName: String,
                              active: Option[Boolean],
                              timeZone: Option[String]
                            )

case class Priority(
                     self: Option[String],
                     iconUrl: Option[String],
                     name: String,
                     id: String
                   )

case class StatusCategory(
                           self: Option[String],
                           id: Long,
                           key: String,
                           colorName: Option[String],
                           name: String
                         )

case class Status(
                   self: Option[String],
                   description: Option[String],
                   iconUrl: Option[String],
                   name: String,
                   id: String,
                   statusCategory: Option[StatusCategory]
                 )

case class Votes(
                  self: Option[String],
                  votes: Long,
                  hasVoted: Option[Boolean]
                )

case class Issuetype(
                      self: Option[String],
                      id: String,
                      description: String,
                      iconUrl: Option[String],
                      name: String,
                      subtask: Option[Boolean]
                    )

case class Project(
                    self: Option[String],
                    id: String,
                    key: String,
                    name: String,
                    avatarUrls: Option[AvatarUrls]
                  )

case class Watches(
                    self: Option[String],
                    watchCount: Option[Long],
                    isWatching: Option[Boolean]
                  )

case class Fields(
                   customfield_13062: Option[Customfield_13062],
                   fixVersions: Option[List[FixVersions]],
                   resolution: Option[Resolution],
                   customfield_13502: Option[List[Customfield_13502]],
                   customfield_10107: Option[Customfield_13062],
                   lastViewed: Option[String],
                   priority: Option[Priority],
                   labels: Option[List[String]],
                   customfield_10103: Option[String],
                   versions: Option[List[FixVersions]],
                   assignee: Option[Customfield_13502],
                   status: Option[Status],
                   customfield_15900: Option[Customfield_13062],
                   customfield_17801: Option[String],
                   reporter: Option[Customfield_13502],
                   customfield_10200: Option[Customfield_13062],
                   customfield_14800: Option[String],
                   votes: Option[Votes],
                   issuetype: Option[Issuetype],
                   project: Project,
                   customfield_13301: Option[String],
                   customfield_10302: Option[String],
                   resolutiondate: Option[String],
                   watches: Option[Watches],
                   created: Option[String],
                   customfield_13405: Option[Customfield_13062],
                   updated: Option[String],
                   customfield_13001: Option[List[Customfield_13062]],
                   description: Option[String],
                   customfield_15701: Option[String],
                   customfield_10802: Option[List[Customfield_13062]],
                   summary: Option[String],
                   customfield_13075: Option[Customfield_13062],
                   customfield_13065: Option[List[Customfield_13062]],
                   customfield_13067: Option[Customfield_13062]
                 )

case class Issues(
                   expand: Option[String],
                   id: String,
                   self: Option[String],
                   key: String,
                   fields: Option[Fields]
                 )

case class JiraResponse(
                         expand: Option[String],
                         startAt: Option[Long],
                         maxResults: Option[Long],
                         total: Long,
                         issues: List[Issues]
                       )


object JiraResponse {


  implicit def JiraIssuesToBgBug(in: Issues): Bgbug = Bgbug(
    //
    in.fields.map(_.created.getOrElse("")).getOrElse(""), //    `Detected on Date`: String,
    None, //    `Modif_to_Reopen`: Option[String],
    None, //    `Bucket`: Option[String],
    None, //    `Time_Stamp_New`: Option[String],
    None, //    `Affected Fields`: Option[String],
    None, //    `Actual Fix Time`: Option[String],
    None, //    `Closing Date`: Option[String],
    None, //    `Prior_Fixed`: Option[String],
    Some(in.fields.get.project.name), //    `FRSD Name`: Option[String],
    Some(in.fields.get.project.name), //    `Main Project`: Option[String],
    in.fields.flatMap(_.summary), //    `Subject`: Option[String],
    None, //    `Comments`: Option[String],
    Some(in.fields.get.project.name), //    `Original Detected in Project`: Option[String],
    None, //    `Delivery Date`: Option[String],
    in.fields.flatMap(_.reporter.flatMap(_.emailAddress)), //    `Detected By`: Option[String],
    None, //    `Close Counter Internal`: Option[String],
    in.fields.flatMap(_.status.map(_.name)), //    `Sub Status`: Option[String],
    None, //    `QA (last)`: Option[String],
    None, //    `Previous Group`: Option[String],
    in.fields.flatMap(_.description), //    `Description`: Option[String],
    None, //    `Technical Component`: Option[String],
    None, //    `Product/Project`: Option[String],
    None, //    `Testing Phase`: Option[String],
    None, //    `ReTest Failed Counter`: Option[String],
    None, //    `FRSD Type`: Option[String],
    None, //    `Detected in time`: Option[String],
    in.id.toLong, //    `Defect ID`: Long,
    None, //`View`: Option[String],
    None, // `Open_date`: Option[String],
    None, // `Number of 'ReOpen'`: Option[String],
    Some(in.fields.get.project.name), // `Detected in Project`: Option[String],
    in.fields.map(_.project.name), //    `Group`: Option[String],
    None, //    `Customer`: Option[String],
    in.fields.flatMap(_.priority.map(_.name)), //    `Priority`: Option[String],
    None, //    `Is_Old`: Option[String],
    None, //    `Prior Defect Status`: Option[String],
    None, //    `Prevent Copy`: Option[String],
    None, //    `Time_Stamp_Old`: Option[String],
    None, //    `All Relevant Info Supplied`: Option[String],
    None, //    `Check Field`: Option[String],
    None, //    `Detected in Release`: Option[String],
    None, //    `NFR  Rejected by Fund`: Option[String],
    None, //    `Last Leg`: Option[String],
    None, //    `Product`: Option[String],
    None, //    `Assigned To`: Option[String],
    in.fields.flatMap(_.summary), //    `Summary`: Option[String],
    None, //    `Close Counter External`: Option[String],
    None, //    `Developer (last)`: Option[String],
    None, //    `Severity`: Option[String],
    None, //    `Modified`: Option[String],
    None, //    `Status`: Option[String],
    in.fields.flatMap(_.issuetype.map(_.name)), //    `Type`: Option[String],
    in.fields.flatMap(_.reporter.flatMap(_.emailAddress)), //    `Raised By`: Option[String],
    None, //    Suggested_Resolution: Option[String],
    Some(in.key), //    `JiraKey`: Option[String],


    //    Some(in.fields.get.fixVersions.get.mkString(",")),
    //    in.fields.map(_.fixVersions.mkString()),


  )

  implicit def JiraIssuesListToBgBug(in: List[Issues]): List[Bgbug] = {
    in.map(JiraIssuesToBgBug(_))
  }
}

case class Index(
                  _index: String,
                  _type: String,
                  _id: String
                )

case class ESBulkIndex(
                        index: Index
                      )
