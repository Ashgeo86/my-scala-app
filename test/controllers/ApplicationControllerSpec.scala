package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.http.Status
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._

class ApplicationControllerSpec
  extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "ApplicationController.index" should {
    "return NOT_IMPLEMENTED" in {

      val testApplicationController =
        app.injector.instanceOf[ApplicationController]

      val result =
        testApplicationController.index()(FakeRequest())

      status(result) mustBe Status.NOT_IMPLEMENTED
    }
  }
}