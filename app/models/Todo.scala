package models

import java.time.LocalDateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._

import ixias.model._
import ixias.util.EnumStatus

import Todo._

/**
 * Todoケースクラス
 * @param id Todo識別子
 * @param title Todoタイトル
 * @param body Todoの内容
 * @param state 状態
 * @param createdAt Todo作成日
 * @param updatedAt Todo更新日
 */
case class Todo (
  id:         Option[Id],
  categoryId: TodoCategory.Id,
  title:      String,
  body:       String,
  state:      Short,
  createdAt:  LocalDateTime = NOW,
  updatedAt:  LocalDateTime = NOW
) extends EntityModel[Id]

/**
 * Todoコンパニオンオブジェクト
 */
object Todo {
  val Id = the[Identity[Id]]
  type Id = Long @@ Todo
  type WithNoId = Entity.WithNoId[Id, Todo]
  type EmbeddedId = Entity.EmbeddedId[Id, Todo]

  def apply(categoryId: TodoCategory.Id, title: String, body: String, state: Short): Todo#WithNoId =
    new Todo(
      id = None,
      categoryId,
      title,
      body,
      state
    ).toWithNoId

  def toJson(todo: Todo): JsValue = Json.toJson(todo)

//  implicit val idWriter: Writes[TodoId] = Writes[TodoId] { id => JsNumber(id.value) }

  implicit val idWriter: Writes[Id] = Writes[Id] { id => JsNumber(id) }
  implicit val categoryIdWriter: Writes[TodoCategory.Id] = Writes[TodoCategory.Id] { id => JsNumber(id) }

  implicit val writer: Writes[Todo] = (
      (JsPath \ "id").write[Option[Id]] and
      (JsPath \ "categoryId").write[TodoCategory.Id] and
      (JsPath \ "title").write[String] and
      (JsPath \ "body").write[String] and
      (JsPath \ "state").write[Short] and
      (JsPath \ "createdAt").write[LocalDateTime] and
      (JsPath \ "updatedAt").write[LocalDateTime]
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