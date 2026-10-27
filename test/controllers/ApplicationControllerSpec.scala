package controllers

import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ApplicationService, RepositoryService}

import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec
  extends PlaySpec
    with MockFactory {

  implicit val ec: ExecutionContext =
    ExecutionContext.global

  "ApplicationController index" should {

    "return OK and a JSON list of books" in {
      val repoService = mock[RepositoryService]
      val appService = mock[ApplicationService]

      val controller = new ApplicationController(
        stubControllerComponents(),
        repoService,
        appService
      )

      val books = Seq(
        DataModel("1", "Test Book", "A test description", 100)
      )

      (repoService.index _)
        .expects()
        .returning(Future.successful(Right(books)))

      val result = controller.index()(FakeRequest(GET, "/api"))

      status(result) mustBe OK
      contentAsJson(result) mustBe Json.toJson(books)
    }

    "return the API error status when the repository fails" in {
      val repoService = mock[RepositoryService]
      val appService = mock[ApplicationService]

      val controller = new ApplicationController(
        stubControllerComponents(),
        repoService,
        appService
      )

      val error = APIError.NotFound("Books not found")

      (repoService.index _)
        .expects()
        .returning(Future.successful(Left(error)))

      val result = controller.index()(FakeRequest(GET, "/api"))

      status(result) mustBe NOT_FOUND
    }
  }

  "ApplicationController addBookForm" should {
    "save a valid book submitted through the browser form" in {
      val repoService = mock[RepositoryService]
      val appService = mock[ApplicationService]

      val controller = new ApplicationController(
        stubControllerComponents(),
        repoService,
        appService
      )

      val savedBook = DataModel(
        "saved-book-id",
        "Test Book",
        "A test description",
        120
      )

      (repoService.create _)
        .expects(where { (book: DataModel) =>
          book.name == "Test Book" &&
            book.description == "A test description" &&
            book.pageCount == 120
        })
        .returning(Future.successful(Right(savedBook)))

      val request = FakeRequest(POST, "/books/new")
        .withFormUrlEncodedBody(
          "name" -> "Test Book",
          "description" -> "A test description",
          "pageCount" -> "120"
        )

      val result = controller.addBookForm()(request)

      status(result) mustBe CREATED
      contentAsJson(result) mustBe Json.toJson(savedBook)
    }
  }

}