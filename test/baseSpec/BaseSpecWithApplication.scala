package baseSpec

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import services.MongoService

abstract class BaseSpecWithApplication
  extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  override def fakeApplication() =
    new GuiceApplicationBuilder().build()

  def mongoService: MongoService =
    app.injector.instanceOf[MongoService]
}