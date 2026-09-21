package repositories

import models.DataModel
import org.bson.conversions.Bson
import org.mongodb.scala._
import org.mongodb.scala.bson.Document
import org.mongodb.scala.model._
import org.mongodb.scala.result.{DeleteResult, UpdateResult}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(
                                mongoService: services.MongoService
                              )(implicit ec: ExecutionContext) {

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

  def index(): Future[Either[Int, Seq[DataModel]]] =
    collection
      .find()
      .toFuture()
      .map { documents =>
        Right(documents.map(fromDocument))
      }

  def create(dataModel: DataModel): Future[DataModel] =
    collection
      .insertOne(toDocument(dataModel))
      .toFuture()
      .map(_ => dataModel)

  private def byId(id: String): Bson =
    Filters.equal("_id", id)

  def read(id: String): Future[Option[DataModel]] =
    collection
      .find(byId(id))
      .headOption()
      .map(_.map(fromDocument))

  def update(
              id: String,
              dataModel: DataModel
            ): Future[UpdateResult] =
    collection
      .replaceOne(
        filter = byId(id),
        replacement = toDocument(dataModel),
        options = new ReplaceOptions().upsert(false)
      )
      .toFuture()

  def delete(id: String): Future[DeleteResult] =
    collection
      .deleteOne(byId(id))
      .toFuture()

  def deleteAll(): Future[Unit] =
    collection
      .deleteMany(Filters.empty())
      .toFuture()
      .map(_ => ())
}