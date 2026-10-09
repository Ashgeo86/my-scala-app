package controllers

import models.{APIError, DataModel}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}
import services.{ApplicationService, RepositoryService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       repoService: RepositoryService,
                                       service: ApplicationService
                                     )(implicit ec: ExecutionContext) extends BaseController {

  private def handleResult[A](
                               result: Future[Either[APIError, A]]
                             )(onSuccess: A => Result): Future[Result] =
    result.map {
      case Right(value) =>
        onSuccess(value)

      case Left(error) =>
        Status(error.httpResponseStatus)(
          Json.toJson(error.reason)
        )
    }

  def index(): Action[AnyContent] =
    Action.async {
      handleResult(repoService.index()) { books =>
        Ok(Json.toJson(books))
      }
    }

  def create(): Action[JsValue] =
    Action.async(controllerComponents.parsers.json) { request =>
      request.body.validate[DataModel] match {
        case JsSuccess(dataModel, _) =>
          handleResult(repoService.create(dataModel)) { book =>
            Created(Json.toJson(book))
          }

        case JsError(_) =>
          Future.successful(BadRequest)
      }
    }

  def read(id: String): Action[AnyContent] =
    Action.async {
      handleResult(repoService.read(id)) { book =>
        Ok(Json.toJson(book))
      }
    }

  def findByName(name: String): Action[AnyContent] =
    Action.async {
      handleResult(repoService.findByName(name)) { books =>
        Ok(Json.toJson(books))
      }
    }

  def update(id: String): Action[JsValue] =
    Action.async(controllerComponents.parsers.json) { request =>
      request.body.validate[DataModel] match {
        case JsSuccess(dataModel, _) =>
          handleResult(repoService.update(id, dataModel)) { _ =>
            Accepted(Json.toJson(dataModel))
          }

        case JsError(_) =>
          Future.successful(BadRequest)
      }
    }

  def updateField(
                   id: String,
                   field: String
                 ): Action[JsValue] =
    Action.async(controllerComponents.parsers.json) { request =>
      request.body.validate[String] match {
        case JsSuccess(value, _) =>
          handleResult(repoService.updateField(id, field, value)) { _ =>
            Accepted
          }

        case JsError(_) =>
          Future.successful(BadRequest)
      }
    }

  def delete(id: String): Action[AnyContent] =
    Action.async {
      handleResult(repoService.delete(id)) { _ =>
        NoContent
      }
    }

  def getGoogleBook(isbn: String): Action[AnyContent] =
    Action.async { implicit request =>
      service
        .getGoogleBook(isbn)
        .fold(
          error =>
            Status(error.httpResponseStatus)(
              Json.toJson(error.reason)
            ),
          book =>
            Ok(Json.toJson(book))
        )
    }
}