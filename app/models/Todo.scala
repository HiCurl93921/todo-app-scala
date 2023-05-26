package models

import java.time.LocalDateTime
import Todo._
import ixias.model._
import ixias.util.EnumStatus
import play.api.data.Form
import play.api.data.Forms._

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

    def display: Seq[(String, String)] = values map { state => (state.code.toString, state.name) }
  }
}

/**
 * 表示用Todoオブジェクト
 * @param id TodoID
 * @param title Todoタイトル
 * @param body Todo詳細
 * @param state Todo状態
 * @param category Todoカテゴリ
 */
case class DisplayTodo (
  id:       Todo.Id,
  title:    String,
  body:     String,
  state:    Todo.State,
  category: TodoCategory.EmbeddedId)

object DisplayTodo {
  def apply (todo: Todo.EmbeddedId, category: TodoCategory.EmbeddedId): DisplayTodo = {
    val todoValue = todo.v
    val state = Todo.State(todoValue)
    new DisplayTodo(todo.id, todoValue.title, todoValue.body, state, category)
  }
}

/**
 * 新規作成用のTodoオブジェクト
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

case class EditingTodo(
  categoryId: Long,
  title:      String,
  body:       String,
  state:      Short
) {
  def to(id: Long): Todo#EmbeddedId = Todo(
    Option(Todo.Id(id)),
    TodoCategory.Id(categoryId),
    title,
    body,
    state
  ).toEmbeddedId
}

object EditingTodo {
  val form: Form[EditingTodo] = Form(
    mapping(
      "categoryId" -> longNumber,
      "title" -> nonEmptyText,
      "body" -> text,
      "state" -> shortNumber
    )(EditingTodo.apply)(EditingTodo.unapply)
  )

  def form(todo: Todo#EmbeddedId): Form[EditingTodo] = form.fill(of(todo))

  def of(todo: Todo#EmbeddedId): EditingTodo = EditingTodo(todo.v.categoryId, todo.v.title, todo.v.body, todo.v.state)
}