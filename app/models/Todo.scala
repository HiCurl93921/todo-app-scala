package models

import java.time.LocalDateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._

/**
 * Todo識別子エンティティ
 * @param value ID値
 */
case class TodoId (
    value: Long
) extends AnyVal

/**
 * Todoケースクラス
 * @param id Todo識別子
 * @param title Todoタイトル
 * @param body Todoの内容
 * @param createdAt Todo作成日
 * @param updateAt Todo更新日
 */
case class Todo (
  id:         TodoId,
  title:      String,
  body:       String,
  createdAt:  LocalDateTime,
  updateAt:   LocalDateTime
)

/**
 * Todoコンパニオンオブジェクト
 */
object Todo {
  def toJson(todo: Todo): JsValue = Json.toJson(todo)

  implicit val idWriter: Writes[TodoId] = Writes[TodoId] { id => JsNumber(id.value) }

  implicit val writer: Writes[Todo] = (
      (JsPath \ "id").write[TodoId] and
      (JsPath \ "title").write[String] and
      (JsPath \ "body").write[String] and
      (JsPath \ "createdAt").write[LocalDateTime] and
      (JsPath \ "updateAt").write[LocalDateTime]
    ) (unlift(Todo.unapply)
  )
}

case class TodoInput (
  title:  String,
  body:   Option[String]
)

object TodoInput {
  implicit val reads = (
    (JsPath \ "title").read(minLength[String](1) keepAnd maxLength[String](256)) and
      (JsPath \ "body").readNullable[String]
    )(apply _)
}

/**
 * 更新用Todoケースクラス
 * @param title Todoタイトル
 * @param body Todoの内容
 */
case class TodoUpdate (
  title:  String,
  body:   String
)

/**
 * 更新用Todoコンパニオンオブジェクト
 */
object TodoUpdate {
  implicit val reads = (
    (JsPath \ "title").read(minLength[String](1) keepAnd( maxLength[String](256))) and
      (JsPath \ "body").read(minLength[String](0) keepAnd maxLength[String](256))
  )(apply _)
}