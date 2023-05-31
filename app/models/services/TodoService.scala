package models.services

import models.todos.{ResponseTodo, Todo}

import scala.concurrent.{ExecutionContext, Future}

class TodoService()(implicit executionContext: ExecutionContext) {
  private val todoRepository = persistence.onMySql.TodoRepository.repository
  private val categoryRepository = persistence.onMySql.TodoCategoryRepository.repository

  def get(): Future[Seq[ResponseTodo]] = for {
    reservedTodos <- todoRepository.get()
    reservedCategories <- categoryRepository.get {
      reservedTodos.map {
        _.v.categoryId
      }.distinct
    }
  } yield {
    ResponseTodo(reservedTodos, reservedCategories)
  }

  def get(id: Todo.Id): Future[Option[ResponseTodo]] = todoRepository.get(id) flatMap {
    joinCategory
  }

  private def joinCategory(todo: Option[Todo.EmbeddedId]): Future[Option[ResponseTodo]] = todo match {
    case None => Future.successful(None)
    case Some(todo) => categoryRepository.get(todo.v.categoryId) map { category =>
      Some(ResponseTodo(todo, category))
    }
  }

  def add(todo: Todo.WithNoId): Future[Todo.Id] = todoRepository.add(todo)

  def update(updating: Todo.EmbeddedId): Future[Option[ResponseTodo]] = todoRepository.update(updating) flatMap {
    case None => Future.successful(None)
    case Some(old) => get(old.id)
  }

  def delete(id: Todo.Id): Future[Option[Todo.EmbeddedId]] = todoRepository.remove(id)
}
