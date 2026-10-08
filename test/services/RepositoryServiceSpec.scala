package services

import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.play.PlaySpec
import repositories.DataRepository

import scala.concurrent.Future

class RepositoryServiceSpec
  extends PlaySpec
    with MockFactory {

  "RepositoryService" should {

    "return books from the repository" in {

      val mockRepository = mock[DataRepository]

      val repositoryService =
        new RepositoryService(mockRepository)(
          scala.concurrent.ExecutionContext.global
        )

      val books = Seq(
        DataModel(
          "1",
          "Test Book",
          "A test book",
          100
        )
      )

      (mockRepository.index _)
        .expects()
        .returning(Future.successful(Right(books)))

      repositoryService.index().futureValue mustBe Right(books)
    }

    "return repository errors" in {

      val mockRepository = mock[DataRepository]

      val repositoryService =
        new RepositoryService(mockRepository)(
          scala.concurrent.ExecutionContext.global
        )

      val error =
        APIError.NotFound("Books cannot be found")

      (mockRepository.index _)
        .expects()
        .returning(Future.successful(Left(error)))

      repositoryService.index().futureValue mustBe Left(error)
    }
  }
}