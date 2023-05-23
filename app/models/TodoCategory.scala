package models

import ixias.model._

import java.time.LocalDateTime

import TodoCategory._

case class TodoCategory (
  id:         Option[Id],
  name:       String,
  slug:       String,
  color:      Short,
  updatedAt:  LocalDateTime = NOW,
  createdAt:  LocalDateTime = NOW
) extends EntityModel[Id]

object TodoCategory {
  val Id = the[Identity[Id]]
  type Id = Long @@ TodoCategory
  type WithNoId = Entity.WithNoId[Id, TodoCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, TodoCategory]

  def apply (name: String, slug: String, color: Short): TodoCategory#WithNoId =
    new TodoCategory(
      id = None,
      name,
      slug,
      color
    ).toWithNoId
}