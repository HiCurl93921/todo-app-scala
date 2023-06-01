package models.categories

import models.Color
import play.api.libs.json.{Json, Writes}

case class ResponseCategory(
  id:    TodoCategory.Id,
  name:  String,
  slug:  String,
  color: Color
)

object ResponseCategory {
  implicit val categoryWriter: Writes[ResponseCategory] = Json.writes[ResponseCategory]

  def apply(category: TodoCategory.EmbeddedId): ResponseCategory = ResponseCategory(
    category.id,
    category.v.name,
    category.v.slug,
    category.v.color
  )
}