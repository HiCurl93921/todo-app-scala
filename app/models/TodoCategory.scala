package models

import ixias.model._

import java.time.LocalDateTime

import TodoCategory._

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

case class SelectableCategory(id: TodoCategory.Id, name: String)

class SelectableCategories(categories: Seq[TodoCategory#EmbeddedId]) extends IndexedSeq[SelectableCategory] {
  private lazy val selectableCategories: IndexedSeq[SelectableCategory] = categories map { category =>
    SelectableCategory(id = category.id, name = category.v.name)
  } toIndexedSeq

  override def length: Int = selectableCategories.length

  override def apply (idx: Int): SelectableCategory = selectableCategories(idx)

  def display: IndexedSeq[(String, String)] = selectableCategories map { category => (category.id.toString, category.name)}
}