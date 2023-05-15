package persistence

import models.{Todo, TodoId, TodoInput, TodoUpdate}

import scala.concurrent.Future

trait TodoRepository {
    def all(): Future[Seq[Todo]]
    def findById(todoId: TodoId): Future[Option[Todo]]
    def create(input: TodoInput): Future[Todo]
    def update(id: TodoId, update: TodoUpdate): Future[Todo]
    def delete(id: TodoId): Future[Unit]
}