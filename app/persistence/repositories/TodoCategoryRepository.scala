package persistence.repositories

import ixias.persistence.SlickRepository
import models.TodoCategory
import persistence.db.SlickResourceProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

case class TodoCategoryRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[TodoCategory.Id, TodoCategory, P]
  with SlickResourceProvider[P]
  {
    import api._

    def get(id: Id): Future[Option[EntityEmbeddedId]] =
      RunDBAction(TodoCategoryTable, "slave") { query =>
        query.filter(q => q.id === id)
          .result
          .headOption
      }

    def add (entity: EntityWithNoId): Future[Id] =
      RunDBAction(TodoCategoryTable) { slick =>
        slick returning slick.map(_.id) += entity.v
      }

    def update (entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
      RunDBAction(TodoCategoryTable) { slick =>
        val row = slick.filter(_.id === entity.id)
        for {
          old <- row.result.headOption
          _ <- old match {
            case None => DBIO.successful(0)
            case Some(_) => row.update(entity.v)
          }
        } yield old
      }

    def remove (id: Id): Future[Option[EntityEmbeddedId]] =
      RunDBAction(TodoCategoryTable) { slick =>
        val row = slick.filter(_.id === id)
        for {
          old <- row.result.headOption
          _ <- old match {
            case None => DBIO.successful(0)
            case Some(_) => row.delete
          }
        } yield old
      }
}
