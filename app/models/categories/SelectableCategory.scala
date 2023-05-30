package models.categories


case class SelectableCategory(id: TodoCategory.Id, name: String)

class SelectableCategories(categories: Seq[TodoCategory#EmbeddedId]) extends IndexedSeq[SelectableCategory] {
  private lazy val selectableCategories: IndexedSeq[SelectableCategory] = categories map { category =>
    SelectableCategory(id = category.id, name = category.v.name)
  } toIndexedSeq

  override def length: Int = selectableCategories.length

  override def apply (idx: Int): SelectableCategory = selectableCategories(idx)

  def display: IndexedSeq[(String, String)] = selectableCategories map { category => (category.id.toString, category.name)}
}