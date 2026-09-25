package controllers

import baseSpec.BaseSpecWithApplication
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ApplicationControllerSpec
  extends BaseSpecWithApplication {

  "ApplicationController.index" should {
    "return OK" in {

      val testApplicationController = new ApplicationController(
        component,
        repository
      )

      val result =
        testApplicationController.index()(FakeRequest())

      status(result) mustBe Status.OK
    }
  }
}