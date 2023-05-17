package persistence

import javax.inject.Singleton
import java.time.LocalDateTime
import models.{Todo, TodoId, TodoInput, TodoUpdate}
import org.joda.time.IllegalInstantException
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class TodoRepositoryOnmMemoryImpl() extends TodoRepository {
    private val todos: OnMemoryTodos = new OnMemoryTodos()

    def all(): Future[Seq[Todo]] = Future { todos.all() }
    def findById(todoId: TodoId): Future[Option[Todo]] = Future { todos.find(todoId) }
    def create(input: TodoInput): Future[Todo] = Future {
        val todo = createTodo(input)

        todos.add(todo) fold(ex => throw ex, identity)
    }

    def update(id: TodoId, update: TodoUpdate): Future[Todo] = Future {
        todos.update(id, update) fold(ex => throw ex, identity)
    }

    def delete(id: TodoId): Future[Unit] = Future { todos.delete(id) }

    private def createTodo(input: TodoInput): Todo = {
        val current = currentDateTime()
        Todo(id = todos.nextId, title = input.title, body = input.body.getOrElse(""), createdAt = current, updateAt = current)
    }

    private def currentDateTime() = LocalDateTime.now()
}

private class OnMemoryTodos {
    private val dateTime = LocalDateTime.now()
    private var todos: Seq[Todo] = Seq(
        Todo(id = TodoId(1), title = "todo1", body = "", createdAt = dateTime, updateAt = dateTime),
        Todo(id = TodoId(2), title = "todo2", body = "", createdAt = dateTime, updateAt = dateTime),
        Todo(id = TodoId(3), title = "todo3", body = "", createdAt = dateTime, updateAt = dateTime),
        Todo(id = TodoId(4), title = "todo4", body = "", createdAt = dateTime, updateAt = dateTime)
    )

    def all(): scala.collection.Seq[Todo] = todos.toSeq

    def find(id: TodoId): Option[Todo] = todos find { _.id == id}

    def add(todo: Todo): Try[Todo] = Try {
        if (exists(todo)) {
            throw new IllegalInstantException("重複する要素を追加できません")
        }
        todos = todos :+ todo
        todo
    }

    def update(id: TodoId, update: TodoUpdate): Try[Todo] = Try {
        val todo = find(id).fold(
            throw new IllegalInstantException(s"id:${id.value}のTodoが存在しません。")
        )(value =>
            value.copy(title = update.title, body = update.body, updateAt = currentDateTime())
        )
        delete(id)
        todos = todos :+ todo
        todo
    }

    def delete(id: TodoId): Try[Unit] = Try {
        if (!exists(id)) {
            throw new IllegalInstantException(s"id:${id.value}のTodoを削除しようとしましたが、存在しませんでした。")
        }

        todos = todos.filter(_.id != id)
    }

    def nextId: TodoId = {
        val maxId = todos map { todo => todo.id.value } max

        TodoId(maxId + 1)
    }

    def exists(todo: Todo): Boolean = exists((todo.id))

    def exists(id: TodoId): Boolean = todos.exists((_.id == id))

    private def currentDateTime () = LocalDateTime.now()
}