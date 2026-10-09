package connectors

import cats.data.EitherT
import models.APIError
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.libs.ws.{WSClient, WSResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

case class GoogleApiErrorResponse(
                                   error: GoogleApiErrorDetails
                                 )

case class GoogleApiErrorDetails(
                                  code: Int,
                                  message: String
                                )

object GoogleApiErrorResponse {
  implicit val detailsReads: Reads[GoogleApiErrorDetails] =
    Json.reads[GoogleApiErrorDetails]

  implicit val reads: Reads[GoogleApiErrorResponse] =
    Json.reads[GoogleApiErrorResponse]
}

@Singleton
class LibraryConnector @Inject()(ws: WSClient) {

  private val logger = Logger(getClass)

  def get[Response](url: String)(
    implicit rds: Reads[Response],
    ec: ExecutionContext
  ): EitherT[Future, APIError, Response] = {
    EitherT {
      ws.url(url)
        .get()
        .map(handleResponse[Response])
        .recover {
          case NonFatal(error) =>
            logger.error(
              s"Google Books request failed: ${error.getMessage}"
            )

            Left(
              APIError.BadAPIResponse(
                503,
                "Google Books is currently unavailable"
              )
            )
        }
    }
  }

  private def handleResponse[Response](
                                        response: WSResponse
                                      )(implicit rds: Reads[Response]): Either[APIError, Response] = {

    if (response.status >= 200 && response.status < 300) {
      response.json.validate[Response] match {
        case JsSuccess(value, _) =>
          Right(value)

        case JsError(errors) =>
          logger.error(
            s"Could not parse Google Books response: $errors"
          )

          Left(
            APIError.BadAPIResponse(
              502,
              "Invalid response from Google Books"
            )
          )
      }
    } else {
      val googleError =
        response.json.validate[GoogleApiErrorResponse]

      val message = googleError match {
        case JsSuccess(error, _) =>
          error.error.message

        case JsError(_) =>
          "Google Books returned an error response"
      }

      val status = response.status match {
        case 400 => 400
        case 404 => 404
        case 429 => 503
        case code if code >= 500 => 503
        case _ => 502
      }

      logger.warn(
        s"Google Books returned HTTP ${response.status}: $message"
      )

      Left(
        APIError.BadAPIResponse(
          status,
          s"Google Books error: $message"
        )
      )
    }
  }
}
