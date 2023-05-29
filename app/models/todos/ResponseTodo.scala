package models.todos

case class ResponseTodo (
  id:         Todo.Id,
  categoryId: TodoCategory.Id,
  title:      String,
  body:       String,
  state:      Short,
)
