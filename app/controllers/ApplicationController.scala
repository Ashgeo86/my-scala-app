package controllers

import models.{APIError, DataModel, NewBookForm}
import play.api.data.{Form, Forms}
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.mvc._
import play.api.i18n.I18nSupport
import services.{ApplicationService, RepositoryService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       repoService: RepositoryService,
                                       service: ApplicationService
                                     )(implicit ec: ExecutionContext)
  extends BaseController
    with I18nSupport {

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

  private def withJsonBody[A: Reads](
                                      onValid: A => Future[Result]
                                    ): Action[JsValue] =
    Action.async(controllerComponents.parsers.json) { request =>
      request.body.validate[A].fold(
        _ =>
          Future.successful(
            BadRequest(Json.obj("error" -> "Invalid request body"))
          ),
        onValid
      )
    }

  def index(): Action[AnyContent] =
    Action.async {
      handleResult(repoService.index()) { books =>
        Ok(Json.toJson(books))
      }
    }

  def create(): Action[JsValue] =
    withJsonBody[DataModel] { dataModel =>
      handleResult(repoService.create(dataModel)) { book =>
        Created(Json.toJson(book))
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
    withJsonBody[DataModel] { dataModel =>
      handleResult(repoService.update(id, dataModel)) { _ =>
        Accepted(Json.toJson(dataModel))
      }
    }

  def updateField(
                   id: String,
                   field: String
                 ): Action[JsValue] =
    withJsonBody[String] { value =>
      handleResult(repoService.updateField(id, field, value)) { _ =>
        Accepted
      }
    }

  def delete(id: String): Action[AnyContent] =
    Action.async {
      handleResult(repoService.delete(id)) { _ =>
        NoContent
      }
    }

  def getGoogleBook(isbn: String): Action[AnyContent] =
    Action.async {
      handleResult(service.getGoogleBook(isbn).value) { book =>
        Ok(Json.toJson(book))
      }
    }

  private val bookForm: Form[NewBookForm] = Form(
    Forms.mapping(
      "name" -> Forms.nonEmptyText,
      "description" -> Forms.nonEmptyText,
      "pageCount" -> Forms.number(min = 0)
    )(NewBookForm.apply)(NewBookForm.unapply)
  )

  def addBook(): Action[AnyContent] =
    Action { implicit request =>
      Ok(views.html.addBook(bookForm))
    }

  def addBookForm(): Action[AnyContent] =
    Action.async { implicit request =>
      bookForm.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(
            BadRequest(views.html.addBook(formWithErrors))
          ),

        formData => {
          val book = DataModel(
            _id = java.util.UUID.randomUUID().toString,
            name = formData.name,
            description = formData.description,
            pageCount = formData.pageCount
          )

          handleResult(repoService.create(book)) { savedBook =>
            Created(Json.toJson(savedBook))
          }
        }
      )
    }
}