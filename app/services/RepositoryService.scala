package services

import models.APIError.BadAPIResponse
import models.DataModel
import repositories.DataRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RepositoryService @Inject()(
                                   dataRepository: DataRepository
                                 )(implicit ec: ExecutionContext) {

  def index(): Future[Either[BadAPIResponse, Seq[DataModel]]] =
    dataRepository.index()

  def create(dataModel: DataModel): Future[DataModel] =
    dataRepository.create(dataModel)

  def read(id: String): Future[Option[DataModel]] =
    dataRepository.read(id)

  def update(id: String, dataModel: DataModel) =
    dataRepository.update(id, dataModel)

  def delete(id: String) =
    dataRepository.delete(id)
}