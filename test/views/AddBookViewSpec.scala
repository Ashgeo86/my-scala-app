package views

import models.NewBookForm
import org.scalatestplus.play.PlaySpec
import play.api.data.Form
import play.api.data.Forms._
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers.stubMessages

class AddBookViewSpec extends PlaySpec {

  private val bookForm: Form[NewBookForm] =
    Form(
      mapping(
        "name" -> nonEmptyText,
        "description" -> nonEmptyText,
        "pageCount" -> number(min = 0)
      )(NewBookForm.apply)(NewBookForm.unapply)
    )

  "addBook template" should {

    "render the book form fields and submit button" in {
      val html =
        views.html.addBook(bookForm)(
          FakeRequest().withCSRFToken,
          stubMessages()
        )

      val content = html.toString

      content must include("name")
      content must include("description")
      content must include("pageCount")
      content must include("Add Book")
    }

    "display validation errors for an invalid form" in {
      val invalidForm =
        bookForm.bind(
          Map(
            "name" -> "",
            "description" -> "",
            "pageCount" -> "-1"
          )
        )

      val html =
        views.html.addBook(invalidForm)(
          FakeRequest().withCSRFToken,
          stubMessages()
        )

      val content = html.toString

      content must include("error")
      content must include("name")
    }
  }
}