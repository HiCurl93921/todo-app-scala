package models.categories

import models.Color
import play.api.libs.json.{Json, Writes}

case class ResponseCategoryOnTodo (
  name:  String,
  slug:  String,
  color: Color
)

object ResponseCategoryOnTodo {
  implicit val categoryWriter: Writes[ResponseCategoryOnTodo] = Json.writes[ResponseCategoryOnTodo]
}