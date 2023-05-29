package models.categories

import models.Color

case class ResponseCategoryOnTodo (
  name:  String,
  slug:  String,
  color: Color
)