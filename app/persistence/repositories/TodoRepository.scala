package persistence.repositories

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import slick.jdbc.JdbcProfile
import persistence.db.{ SlickResourceProvider, TodoTable }

import models.Todo
case class TodoRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository [Todo.Id, Todo, P]
  with SlickResourceProvider[P]
  {
    import api._

    def get(id: Id): Future[Option[EntityEmbeddedId]] =
      RunDBAction(TodoTable, "slave") { _
        .filter(_.id === id)
        .result
        .headOption
      }

    def get(): Future[Seq[EntityEmbeddedId]] =
      RunDBAction(TodoTable, "slave") { _
        .result
      }

    def add (entity: EntityWithNoId): Future[Id] =
      RunDBAction(TodoTable) { query =>
        query returning query.map(_.id) += entity.v
      }

    def update (entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
      RunDBAction(TodoTable) { query =>
        val row = query.filter(_.id === entity.id)
        for {
          old <- row.result.headOption
          _ <- old match {
            case None     => DBIO.successful(0)
            case Some(_)  => row.update(entity.v)
          }
        } yield old
      }

    def remove (id: Id): Future[Option[EntityEmbeddedId]] =
      RunDBAction(TodoTable) { query =>
        val row = query.filter(_.id === id)
        for {
          old <- row.result.headOption
          _ <- old match {
            case None     => DBIO.successful(0)
            case Some(_)  => row.delete
          }
        } yield old
      }
  }
