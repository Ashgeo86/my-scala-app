package services

import baseSpec.BaseSpecWithApplication
import connectors.LibraryConnector
import models.DataModel
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}

class LibraryServiceSpec
  extends BaseSpecWithApplication
    with MockFactory
    with ScalaFutures {

  val mockConnector = mock[LibraryConnector]

  val testService = new ApplicationService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
  )

  "getGoogleBook returns a book" in {

    val url: String = "testUrl"

    (mockConnector.get[DataModel](_: String)(
      _: OFormat[DataModel],
      _: ExecutionContext
    ))
      .expects(url, *, *)
      .returning(Future(gameOfThrones.as[DataModel]))
      .once()

    whenReady(
      testService.getGoogleBook(
        urlOverride = Some(url),
        search = "",
        term = ""
      )
    ) { result =>
      result shouldBe gameOfThrones.as[DataModel]
    }
  }

  "return an error" in {
    val url: String = "testUrl"

    (mockConnector.get[DataModel](_: String)(
      _: OFormat[DataModel],
      _: ExecutionContext
    ))
      .expects(url, *, *)
      .returning(Future.failed(new RuntimeException("Something went wrong")))
      .once()

    whenReady(
      testService
        .getGoogleBook(urlOverride = Some(url), search = "", term = "")
        .failed
    ) { result =>
      result shouldBe a[RuntimeException]
    }
  }
}
