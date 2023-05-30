package models.todos

import models.categories.{ResponseCategoryForTodo, TodoCategory}
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

  def apply(todo: Todo.EmbeddedId, category: TodoCategory.EmbeddedId): ResponseTodo = new ResponseTodo(
    todo.id,
    ResponseCategoryForTodo(category),
    todo.v.title,
    todo.v.body,
    Todo.State(todo.v.state)
  )

  def apply (todos: Seq[Todo.EmbeddedId], categories: Seq[TodoCategory.EmbeddedId]): Seq[ResponseTodo] = for {
    todo <- todos
    category <- categories.find {
      _.id == todo.v.categoryId
    }
  } yield {
    ResponseTodo(todo, category)
  }
}