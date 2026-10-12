package models

case class NewBookForm(
                        name: String,
                        description: String,
                        pageCount: Int
                      )