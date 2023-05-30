package models.todos

import models.categories.TodoCategory
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.functional.syntax._
import play.api.libs.json.JsPath
import play.api.libs.json.Reads._


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

  implicit val creatingTodoReader = (
    (JsPath \ "categoryId").read[Long] and
      (JsPath \ "title").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
      (JsPath \ "body").read[String]
  )(apply _)
}
