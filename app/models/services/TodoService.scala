package models.services

import models.todos.{ResponseTodo, Todo}

import scala.concurrent.{ExecutionContext, Future}

class TodoService()(implicit executionContext: ExecutionContext) {
  private val todoRepository = persistence.onMySql.TodoRepository.repository

  def get(): Future[Seq[ResponseTodo]] = todoRepository.get() map {
    ResponseTodo(_)
  }

  def get(id: Todo.Id): Future[Option[ResponseTodo]] = todoRepository.get(id) map {
    case Some(todo) => Some(ResponseTodo(todo))
  }

  def add(todo: Todo.WithNoId): Future[Todo.Id] = todoRepository.add(todo)

  def update(updating: Todo.EmbeddedId): Future[Option[ResponseTodo]] = todoRepository.update(updating) flatMap {
    case None => Future.successful(None)
    case Some(old) => get(old.id)
  }

  def delete(id: Todo.Id): Future[Option[Todo.EmbeddedId]] = todoRepository.remove(id)
}
