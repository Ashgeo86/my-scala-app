package models

import play.api.http.Status

sealed abstract class APIError(
                                val httpResponseStatus: Int,
                                val reason: String
                              )

object APIError {

  final case class BadAPIResponse(
                                   upstreamStatus: Int,
                                   upstreamMessage: String
                                 ) extends APIError(
    upstreamStatus,
    s"Bad response from upstream; got status: $upstreamStatus, and got reason $upstreamMessage"
  )

  final case class NotFound(
                             message: String
                           ) extends APIError(
    Status.NOT_FOUND,
    message
  )
}