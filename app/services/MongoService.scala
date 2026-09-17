package services

import com.typesafe.config.Config
import javax.inject._
import org.mongodb.scala._

import scala.concurrent.ExecutionContext

@Singleton
class MongoService @Inject()(
                              config: Config
                            )(implicit ec: ExecutionContext) {

  private val mongoUri: String =
    config.getString("mongodb.uri")

  private val mongoClient: MongoClient =
    MongoClient(mongoUri)

  val database: MongoDatabase =
    mongoClient.getDatabase("my-scala-app")
}