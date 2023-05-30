package models.todos

import models.categories.TodoCategory
import play.api.data.Form
import play.api.data.Forms._

case class EditingTodo(
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

object EditingTodo {
  val form: Form[EditingTodo] = Form(
    mapping(
      "categoryId"  -> longNumber,
      "title"       -> nonEmptyText,
      "body"        -> text,
      "state"       -> shortNumber
    )(EditingTodo.apply)(EditingTodo.unapply)
  )

  def form(todo: Todo#EmbeddedId): Form[EditingTodo] = form.fill(of(todo))

  def of(todo: Todo#EmbeddedId): EditingTodo = EditingTodo(todo.v.categoryId, todo.v.title, todo.v.body, todo.v.state)
}