package models.services

import models.categories.TodoCategory
import models.todos.{ResponseTodo, Todo}

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
}
