package models.todos

import models.categories.ResponseCategoryForTodo
import play.api.libs.json.{Json, Writes}
case class ResponseTodo (
  id:       Todo.Id,
  category: ResponseCategoryForTodo,
  title:    String,
  body:     String,
  state:    Todo.State
)

object ResponseTodo {
  implicit val todoWriter: Writes[ResponseTodo] = Json.writes[ResponseTodo]
}