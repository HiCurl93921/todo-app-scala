package models.services

import models.categories.TodoCategory
import models.todos.{CreatingTodo, ResponseTodo, Todo}

import scala.concurrent.{ExecutionContext, Future}

class TodoService()(implicit executionContext: ExecutionContext) {
  private val todoRepository = persistence.onMySql.TodoRepository.repository
  private val categoryRepository = persistence.onMySql.TodoCategoryRepository.repository

  def get[T](f: (Seq[Todo.EmbeddedId], Seq[TodoCategory.EmbeddedId]) => Seq[T]): Future[Seq[T]] = for {
    reservedTodos <- todoRepository.get()
    reservedCategories <- categoryRepository.get {
      reservedTodos.map {
        _.v.categoryId
      }.distinct
    }
  } yield {
    f(reservedTodos, reservedCategories)
  }

  def get[T](id: Long, f: (Todo.EmbeddedId, Option[TodoCategory.EmbeddedId]) => T): Future[Option[T]] = todoRepository.get(Todo.Id(id)) flatMap {
    joinCategory(_, f)
  }

  private def joinCategory[T](todo: Option[Todo.EmbeddedId], f: (Todo.EmbeddedId, Option[TodoCategory.EmbeddedId]) => T): Future[Option[T]] = todo match {
    case None => Future.successful(None)
    case Some(todo) => categoryRepository.get(todo.v.categoryId) map { category =>
      Some(f(todo, category))
    }
  }

  def add(todo: Todo.WithNoId): Future[Todo.Id] = todoRepository.add(todo)

  def update[T](updating: Todo.EmbeddedId, f: (Todo.EmbeddedId, Option[TodoCategory.EmbeddedId]) => T): Future[Option[T]] = todoRepository.update(updating) flatMap {
    case None => Future.successful(None)
    case Some(old) => get(old.id, f)
  }
}
