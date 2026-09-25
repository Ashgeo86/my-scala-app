package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc.Result

import scala.concurrent.Future

class ApplicationControllerSpec
  extends BaseSpecWithApplication {

  lazy val TestApplicationController = new ApplicationController(
    component,
    repository
  )

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "ApplicationController.index" should {

    "return OK" in {

      val result =
        TestApplicationController.index()(FakeRequest())

      status(result) mustBe Status.OK
    }
  }

  "ApplicationController.create" should {

    "create a book in the database" in {

      val request: FakeRequest[JsValue] =
        buildPost("/api")
          .withBody[JsValue](Json.toJson(dataModel))

      val createdResult: Future[Result] =
        TestApplicationController.create()(request)

      status(createdResult) mustBe Status.CREATED
    }
  }
}