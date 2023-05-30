package models.todos

import models.categories.{ResponseCategoryForTodo, TodoCategory}
import play.api.libs.json.{JsString, Json, Writes}
case class ResponseTodo (
  id:       Todo.Id,
  category: Option[ResponseCategoryForTodo],
  title:    String,
  body:     String,
  state:    Todo.State
)

object ResponseTodo {
  implicit val todoWriter: Writes[ResponseTodo] = Json.writes[ResponseTodo]

  def apply(todo: Todo.EmbeddedId, category: Option[TodoCategory.EmbeddedId]): ResponseTodo = new ResponseTodo(
    todo.id,
    category.map { ResponseCategoryForTodo(_) },
    todo.v.title,
    todo.v.body,
    Todo.State(todo.v.state)
  )

  def apply (todos: Seq[Todo.EmbeddedId], categories: Seq[TodoCategory.EmbeddedId]): Seq[ResponseTodo] = todos map { todo =>
    ResponseTodo(todo, categories.find(_.id == todo.id))
  }
}