package models

import play.api.data._
import play.api.data.Forms._

case class CreatingTodo(categoryId: Long, title: String, body: String) {
  val creatingTodoForm: Form[CreatingTodo] = Form(
    mapping(
      "categoryId" -> longNumber,
      "title" -> nonEmptyText,
      "body" -> text
    ) (CreatingTodo.apply) (CreatingTodo.unapply)
  )
}