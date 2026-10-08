package controllers

import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.{ApplicationService, RepositoryService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       repoService: RepositoryService,
                                       service: ApplicationService
                                     )(implicit ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async {
    repoService.index().map {
      case Right(books) =>
        Ok(Json.toJson(books))

      case Left(error) =>
        Status(error.httpResponseStatus)(
          Json.toJson(error.reason)
        )
    }
  }

  def create(): Action[JsValue] =
    Action.async(controllerComponents.parsers.json) { request =>

      request.body.validate[DataModel] match {

        case JsSuccess(dataModel, _) =>
          repoService.create(dataModel).map {
            case Right(book) =>
              Created(Json.toJson(book))

            case Left(error) =>
              Status(error.httpResponseStatus)(
                Json.toJson(error.reason)
              )
          }

        case JsError(_) =>
          Future.successful(BadRequest)
      }
    }

  def read(id: String): Action[AnyContent] =
    Action.async {

      repoService.read(id).map {

        case Right(book) =>
          Ok(Json.toJson(book))

        case Left(error) =>
          Status(error.httpResponseStatus)(
            Json.toJson(error.reason)
          )
      }
    }

  def findByName(name: String): Action[AnyContent] =
    Action.async {

      repoService.findByName(name).map {

        case Right(books) =>
          Ok(Json.toJson(books))

        case Left(error) =>
          Status(error.httpResponseStatus)(
            Json.toJson(error.reason)
          )
      }
    }

  def update(id: String): Action[JsValue] =
    Action.async(controllerComponents.parsers.json) { request =>

      request.body.validate[DataModel] match {

        case JsSuccess(dataModel, _) =>
          repoService.update(id, dataModel).map {

            case Right(_) =>
              Accepted(Json.toJson(dataModel))

            case Left(error) =>
              Status(error.httpResponseStatus)(
                Json.toJson(error.reason)
              )
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
          repoService.updateField(id, field, value).map {

            case Right(_) =>
              Accepted

            case Left(error) =>
              Status(error.httpResponseStatus)(
                Json.toJson(error.reason)
              )
          }

        case JsError(_) =>
          Future.successful(BadRequest)
      }
    }

  def delete(id: String): Action[AnyContent] =
    Action.async {

      repoService.delete(id).map {

        case Right(_) =>
          Accepted

        case Left(error) =>
          Status(error.httpResponseStatus)(
            Json.toJson(error.reason)
          )
      }
    }

  def getGoogleBook(
                     search: String,
                     term: String
                   ): Action[AnyContent] =
    Action.async { implicit request =>
      service
        .getGoogleBook(search = search, term = term)
        .fold(
          error =>
            Status(error.httpResponseStatus)(
              Json.toJson(error.reason)
            ),
          dataModel =>
            Ok(Json.toJson(dataModel))
        )
    }
}