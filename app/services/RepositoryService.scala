package services

import models.{APIError, DataModel}
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RepositoryService @Inject()(
                                   dataRepository: DataRepository
                                 )(implicit ec: ExecutionContext) {

  def index(): Future[Either[APIError, Seq[DataModel]]] =
    dataRepository.index()

  def create(
              dataModel: DataModel
            ): Future[Either[APIError, DataModel]] =
    dataRepository.create(dataModel)

  def read(
            id: String
          ): Future[Either[APIError, DataModel]] =
    dataRepository.read(id)

  def findByName(
                  name: String
                ): Future[Either[APIError, Seq[DataModel]]] =
    dataRepository.findByName(name)

  def update(
              id: String,
              dataModel: DataModel
            ): Future[Either[APIError, UpdateResult]] =
    dataRepository.update(id, dataModel)

  def updateField(
                   id: String,
                   field: String,
                   value: String
                 ): Future[Either[APIError, UpdateResult]] =
    dataRepository.updateField(id, field, value)

  def delete(
              id: String
            ): Future[Either[APIError, DeleteResult]] =
    dataRepository.delete(id)

  def deleteAll(): Future[Either[APIError, Unit]] =
    dataRepository.deleteAll()
}