
package jiraimporter

import JsonSerializers._

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
                              emailAddress: String,
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