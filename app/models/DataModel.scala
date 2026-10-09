
package models

import play.api.libs.json.{Json, OFormat}
import play.api.data._
import play.api.data.Forms._

case class DataModel(
                      _id: String,
                      name: String,
                      description: String,
                      pageCount: Int
                    )

object DataModel {
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

  val dataModelForm: Form[DataModel] = Form(
    mapping(
      "_id" -> nonEmptyText,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "pageCount" -> number
    )(DataModel.apply)(DataModel.unapply)
  )
}