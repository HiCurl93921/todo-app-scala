package models.todos

import models.categories.TodoCategory

/**
 * 表示用Todoオブジェクト
 *
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
