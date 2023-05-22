package models

import java.time.LocalDateTime
import Todo._
import ixias.model._
import ixias.util.EnumStatus

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

  sealed abstract class State (
    val code: Short,
    val name: String
  ) extends EnumStatus

  /**
  Todo状態列挙型
   */
  object State extends EnumStatus.Of[State] {

    case object NotYet extends State(0, "TODO(未着手)")

    case object Ongoing extends State(1, "着手中")

    case object Completed extends State(2, "完了")

    final val DEFAULT: State = NotYet

    override def apply(code: Short): State = State
      .find(_.code == code)
      .getOrElse(DEFAULT)

    def apply(todo: Todo): State = apply(todo.state)
  }
}