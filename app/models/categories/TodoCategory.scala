package models.categories

import TodoCategory._
import ixias.model.{@@, Entity, EntityModel, Identity, NOW, the}
import models.Color

import java.time.LocalDateTime

case class TodoCategory (
  id:         Option[Id],
  name:       String,
  slug:       String,
  color:      Color,
  updatedAt:  LocalDateTime = NOW,
  createdAt:  LocalDateTime = NOW
) extends EntityModel[Id]

object TodoCategory {
  val Id = the[Identity[Id]]
  type Id = Long @@ TodoCategory
  type WithNoId = Entity.WithNoId[Id, TodoCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, TodoCategory]

  def apply (name: String, slug: String, color: String): TodoCategory#WithNoId =
    new TodoCategory(
      id = None,
      name,
      slug,
      Color(color)
    ).toWithNoId
}