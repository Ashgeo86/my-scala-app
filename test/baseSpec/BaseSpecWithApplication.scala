package baseSpec

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.FakeRequest
import repositories.DataRepository
import services.MongoService

import scala.concurrent.ExecutionContext

abstract class BaseSpecWithApplication
  extends PlaySpec
    with GuiceOneAppPerTest {

  implicit lazy val executionContext: ExecutionContext =
    app.injector.instanceOf[ExecutionContext]

  lazy val component: ControllerComponents =
    app.injector.instanceOf[ControllerComponents]

  lazy val repository: DataRepository =
    app.injector.instanceOf[DataRepository]

  lazy val mongoService: MongoService =
    app.injector.instanceOf[MongoService]

  override def fakeApplication() =
    new GuiceApplicationBuilder().build()

  def buildPost(url: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("POST", url)

  def buildGet(url: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("GET", url)
}