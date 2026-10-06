package services

import baseSpec.BaseSpecWithApplication
import connectors.LibraryConnector
import models.{APIError, DataModel}
import org.apache.pekko.util.Helpers.Requiring
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsValue, Json, OFormat}
import cats.data.EitherT

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
      .returning(
        EitherT.rightT[Future, APIError](gameOfThrones.as[DataModel])
      )
      .once()

    whenReady(
      testService
        .getGoogleBook(
          urlOverride = Some(url),
          search = "",
          term = ""
        )
        .value
    ) { result =>
      result shouldBe Right(gameOfThrones.as[DataModel])
    }
  }

  "getGoogleBook returns an error" in {

    val url: String = "testUrl"

    val error =
      APIError.BadAPIResponse(500, "Something went wrong")

    (mockConnector.get[DataModel](_: String)(
      _: OFormat[DataModel],
      _: ExecutionContext
    ))
      .expects(url, *, *)
      .returning(
        EitherT.leftT[Future, DataModel](error)
      )
      .once()

    whenReady(
      testService
        .getGoogleBook(
          urlOverride = Some(url),
          search = "",
          term = ""
        )
        .value
    ) { result =>
      result shouldBe Left(error)
    }
  }
}