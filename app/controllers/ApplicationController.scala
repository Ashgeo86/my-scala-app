package controllers

import models.DataModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       dataRepository: DataRepository
                                     )(implicit ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(item: Seq[DataModel]) =>
        Ok(Json.toJson(item))

      case Left(error) =>
        Status(error)(Json.toJson("Unable to find any books"))
    }
  }

  def create() = TODO

  def read(id: String) = TODO

  def update(id: String) = TODO

  def delete(id: String) = TODO
}