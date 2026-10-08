package services

import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, DataModel}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationService @Inject()(connector: LibraryConnector) {

  def getGoogleBook(
                     urlOverride: Option[String] = None,
                     search: String,
                     term: String
                   )(implicit ec: ExecutionContext): EitherT[Future, APIError, DataModel] =
    connector.get[DataModel](
      urlOverride.getOrElse(
        s"https://www.googleapis.com/books/v1/volumes?q=$search:$term"
      )
    )
}
