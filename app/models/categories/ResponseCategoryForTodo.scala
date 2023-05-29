package models.categories

import models.Color
import play.api.libs.json.{Json, Writes}

case class ResponseCategoryForTodo (
  name:  String,
  slug:  String,
  color: Color
)

object ResponseCategoryForTodo {
  implicit val categoryWriter: Writes[ResponseCategoryForTodo] = Json.writes[ResponseCategoryForTodo]
}