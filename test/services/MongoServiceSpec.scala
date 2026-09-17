package services

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

class MongoServiceSpec
  extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "MongoService" should {

    "connect to the configured MongoDB database" in {
      val mongoService = app.injector.instanceOf[MongoService]

      mongoService.database.name mustBe "my-scala-app"
    }
  }
}