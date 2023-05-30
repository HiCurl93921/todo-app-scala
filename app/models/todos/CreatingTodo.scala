package models.todos

import models.categories.TodoCategory
import play.api.data.Form
import play.api.data.Forms._


/**
 * 新規作成用のTodoオブジェクト
 *
 * @param categoryId カテゴリID
 * @param title タイトル
 * @param body 詳細
 */
case class CreatingTodo(
  categoryId: Long,
  title:      String,
  body:       String
) {
  def to: Todo#WithNoId = Todo(
    TodoCategory.Id(categoryId),
    title,
    body
  )
}

object CreatingTodo {
  val form: Form[CreatingTodo] = Form(
    mapping(
      "categoryId" -> longNumber,
      "title" -> nonEmptyText,
      "body" -> text
    )(CreatingTodo.apply)(CreatingTodo.unapply)
  )
}
