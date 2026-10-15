package integration

import baseSpec.BaseSpecWithApplication
import models.DataModel
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

class BookApiIntegrationSpec
  extends BaseSpecWithApplication
    with ScalaFutures {

  private val bookId = "integration-test-book-001"

  "Book API" must {

    "create, read, update and delete a book" in {
      repository.deleteAll().futureValue shouldBe Right(())

      val original = DataModel(
        _id = bookId,
        name = "Integration Test Book",
        description = "Original description",
        pageCount = 100
      )

      val createResult = route(
        app,
        FakeRequest(POST, "/api")
          .withJsonBody(Json.toJson(original))
      ).get

      status(createResult) shouldBe CREATED

      // Read
      val readResult =
        route(app, FakeRequest(GET, s"/api/$bookId")).get

      status(readResult) shouldBe OK
      (contentAsJson(readResult) \ "name").as[String] shouldBe
        "Integration Test Book"

      val updated = original.copy(
        name = "Updated Integration Book",
        description = "Updated description",
        pageCount = 150
      )

      val updateResult = route(
        app,
        FakeRequest(PUT, s"/api/$bookId")
          .withJsonBody(Json.toJson(updated))
      ).get

      status(updateResult) shouldBe ACCEPTED

      val rereadResult =
        route(app, FakeRequest(GET, s"/api/$bookId")).get

      status(rereadResult) shouldBe OK
      (contentAsJson(rereadResult) \ "name").as[String] shouldBe
        "Updated Integration Book"
      (contentAsJson(rereadResult) \ "pageCount").as[Int] shouldBe 150

      val deleteResult =
        route(app, FakeRequest(DELETE, s"/api/$bookId")).get

      status(deleteResult) shouldBe NO_CONTENT

      val afterDelete =
        route(app, FakeRequest(GET, s"/api/$bookId")).get

      status(afterDelete) shouldBe NOT_FOUND
    }
  }
}