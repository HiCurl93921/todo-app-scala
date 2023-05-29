package models.todos

import models.categories.ResponseCategoryOnTodo

case class ResponseTodo (
  id:       Todo.Id,
  category: ResponseCategoryOnTodo,
  title:    String,
  body:     String,
  state:    Short
)
