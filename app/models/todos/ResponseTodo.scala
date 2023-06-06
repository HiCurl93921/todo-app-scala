package models.todos

import models.categories.TodoCategory
import play.api.libs.json.{Json, Writes}
case class ResponseTodo (
  id:         Todo.Id,
  categoryId: TodoCategory.Id,
  title:      String,
  body:       String,
  state:      Todo.State
)

object ResponseTodo {
  implicit val todoWriter: Writes[ResponseTodo] = Json.writes[ResponseTodo]

  def apply(todo: Todo.EmbeddedId): ResponseTodo = new ResponseTodo(
    todo.id,
    todo.v.categoryId,
    todo.v.title,
    todo.v.body,
    Todo.State(todo.v.state)
  )

  def apply (todos: Seq[Todo.EmbeddedId]): Seq[ResponseTodo] = todos map { todo =>
    ResponseTodo(todo)
  }
}