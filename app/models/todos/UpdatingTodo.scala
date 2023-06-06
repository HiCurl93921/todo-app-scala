package models.todos

import models.categories.TodoCategory
import play.api.libs.json.JsPath
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

case class UpdatingTodo (
  categoryId: Long,
  title:      String,
  body:       String,
  state:      Short
) {
  def to(id: Long): Todo#EmbeddedId = Todo(
    Option(Todo.Id(id)),
    TodoCategory.Id(categoryId),
    title,
    body,
    state
  ).toEmbeddedId
}

object UpdatingTodo {
  implicit val updatingReader = (
    (JsPath \ "categoryId").read[Long] and
      (JsPath \ "title").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
      (JsPath \ "body").read[String] and
      (JsPath \ "state").read[Short]
  )(apply _)
}