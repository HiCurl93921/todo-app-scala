package models.todos

import ixias.model._
import ixias.util.EnumStatus
import models.todos.Todo._
import models.categories.TodoCategory
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsNumber, JsPath, JsValue, Json, Writes}

import java.time.LocalDateTime

/**
 * Todoケースクラス
 *
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

  implicit val idWriter: Writes[Todo.Id] = Writes[Todo.Id](id => JsNumber(id))

  def apply(categoryId: TodoCategory.Id, title: String, body: String): Todo#WithNoId =
    new Todo(
      id = None,
      categoryId,
      title,
      body,
      State.DEFAULT.code
    ).toWithNoId

  /**
   * Todoの状態を表す列挙型の基底
   * @param code 状態コード番号
   * @param name 状態の表示名称
   */
  sealed abstract class State (
    val code: Short,
    val name: String
  ) extends EnumStatus

  /**
  Todo状態列挙型
   */
  object State extends EnumStatus.Of[State] {
    implicit val stateWriter: Writes[Todo.State] = (
      (JsPath \ "code").write[Short] and
        (JsPath \ "name").write[String]
    )(unlift(State.unapply))

    case object NotYet extends State(0, "TODO(未着手)")
    case object Ongoing extends State(1, "着手中")
    case object Completed extends State(2, "完了")

    final val DEFAULT: State = NotYet

    /**
     * 状態コード番号から、Todo状態オブジェクトを取得します。
     * @param code 状態コード番号
     * @return Todo状態
     */
    override def apply(code: Short): State = State
      .find(_.code == code)
      .getOrElse(DEFAULT)

    /**
     * Todoオブジェクトの状態を検査します。
     * @param todo Todoオブジェクト
     * @return Todoの状態
     */
    def apply(todo: Todo): State = apply(todo.state)

    def unapply(state: State): Option[(Short, String)] = Some((state.code, state.name))

    def display: Seq[(String, String)] = values map { state => (state.code.toString, state.name) }
  }
}
