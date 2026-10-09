package models

import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.{Json, OFormat}

case class DataModel(
                      _id: String,
                      name: String,
                      description: String,
                      pageCount: Int
                    )

object DataModel {
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]
}

case class Person(name: String, surname: String, age: Int)
object Person {
  implicit val formats: OFormat[Person] = Json.format[Person]

  val personForm: Form[Person] = Form(
    mapping(
      "name" -> text,
      "surname" -> text,
      "age" -> number
    )(Person.apply)(Person.unapply)
  )
}