package repositories

import com.google.inject.ImplementedBy
import models.{APIError, DataModel}
import org.bson.conversions.Bson
import org.mongodb.scala._
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model._
import org.mongodb.scala.result.{DeleteResult, UpdateResult}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[PlayMongoRepository])
trait DataRepository {

  def index(): Future[Either[APIError, Seq[DataModel]]]

  def create(
              dataModel: DataModel
            ): Future[Either[APIError, DataModel]]

  def read(
            id: String
          ): Future[Either[APIError, DataModel]]

  def findByName(
                  name: String
                ): Future[Either[APIError, Seq[DataModel]]]

  def update(
              id: String,
              dataModel: DataModel
            ): Future[Either[APIError, UpdateResult]]

  def updateField(
                   id: String,
                   field: String,
                   value: String
                 ): Future[Either[APIError, UpdateResult]]

  def delete(
              id: String
            ): Future[Either[APIError, DeleteResult]]

  def deleteAll(): Future[Either[APIError, Unit]]
}

@Singleton
class PlayMongoRepository @Inject()(
                                     mongoService: services.MongoService
                                   )(implicit ec: ExecutionContext) extends DataRepository {

  private val collection =
    mongoService.database.getCollection[Document]("dataModels")

  private def toDocument(dataModel: DataModel): Document =
    Document(
      "_id" -> dataModel._id,
      "name" -> dataModel.name,
      "description" -> dataModel.description,
      "pageCount" -> dataModel.pageCount
    )

  private def fromDocument(document: Document): DataModel =
    DataModel(
      _id = document.getString("_id"),
      name = document.getString("name"),
      description = document.getString("description"),
      pageCount = document.getInteger("pageCount")
    )

  private def mongoError(error: Throwable): APIError.BadAPIResponse =
    APIError.BadAPIResponse(
      500,
      Option(error.getMessage).getOrElse("MongoDB operation failed")
    )

  private def byId(id: String): Bson =
    Filters.equal("_id", id)

  override def index(): Future[Either[APIError, Seq[DataModel]]] =
    collection
      .find()
      .toFuture()
      .map { documents =>
        if (documents.nonEmpty) {
          Right(documents.map(fromDocument))
        } else {
          Left(APIError.NotFound("Books cannot be found"))
        }
      }
      .recover {
        case error =>
          Left(mongoError(error))
      }

  override def create(
                       dataModel: DataModel
                     ): Future[Either[APIError, DataModel]] =
    collection
      .insertOne(toDocument(dataModel))
      .toFuture()
      .map(_ => Right(dataModel))
      .recover {
        case error =>
          Left(mongoError(error))
      }

  override def read(
                     id: String
                   ): Future[Either[APIError, DataModel]] =
    collection
      .find(byId(id))
      .headOption()
      .map {
        case Some(document) =>
          Right(fromDocument(document))

        case None =>
          Left(APIError.NotFound(s"Book with id $id cannot be found"))
      }
      .recover {
        case error =>
          Left(mongoError(error))
      }

  override def findByName(
                           name: String
                         ): Future[Either[APIError, Seq[DataModel]]] =
    collection
      .find(Filters.equal("name", name))
      .toFuture()
      .map { documents =>
        if (documents.nonEmpty) {
          Right(documents.map(fromDocument))
        } else {
          Left(APIError.NotFound(s"Books with name $name cannot be found"))
        }
      }
      .recover {
        case error =>
          Left(mongoError(error))
      }

  override def update(
                       id: String,
                       dataModel: DataModel
                     ): Future[Either[APIError, UpdateResult]] =
    collection
      .replaceOne(
        filter = byId(id),
        replacement = toDocument(dataModel),
        options = new ReplaceOptions().upsert(false)
      )
      .toFuture()
      .map { result =>
        if (result.getMatchedCount > 0) {
          Right(result)
        } else {
          Left(APIError.NotFound(s"Book with id $id cannot be found"))
        }
      }
      .recover {
        case error =>
          Left(mongoError(error))
      }

  override def updateField(
                            id: String,
                            field: String,
                            value: String
                          ): Future[Either[APIError, UpdateResult]] =
    collection
      .updateOne(
        byId(id),
        Updates.set(field, value)
      )
      .toFuture()
      .map { result =>
        if (result.getMatchedCount > 0) {
          Right(result)
        } else {
          Left(APIError.NotFound(s"Book with id $id cannot be found"))
        }
      }
      .recover {
        case error =>
          Left(mongoError(error))
      }

  override def delete(
                       id: String
                     ): Future[Either[APIError, DeleteResult]] =
    collection
      .deleteOne(byId(id))
      .toFuture()
      .map { result =>
        if (result.getDeletedCount > 0) {
          Right(result)
        } else {
          Left(APIError.NotFound(s"Book with id $id cannot be found"))
        }
      }
      .recover {
        case error =>
          Left(mongoError(error))
      }

  override def deleteAll(): Future[Either[APIError, Unit]] =
    collection
      .deleteMany(Filters.empty())
      .toFuture()
      .map(_ => Right(()))
      .recover {
        case error =>
          Left(mongoError(error))
      }
}