package persistence.repositories

import ixias.persistence.SlickRepository
import models.categories.TodoCategory
import persistence.db.SlickResourceProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait TodoCategoryRepository[P <: JdbcProfile]
  extends SlickRepository[TodoCategory.Id, TodoCategory, P]
  with SlickResourceProvider[P] {
  def get(): Future[Seq[EntityEmbeddedId]]
}
case class TodoCategoryRepositoryOnDataBase[P <: JdbcProfile]()(implicit val driver: P)
  extends TodoCategoryRepository[P]
  {
    import api._

    def get(id: Id): Future[Option[EntityEmbeddedId]] =
      RunDBAction(TodoCategoryTable, "slave") { query =>
      query.filter(q => q.id === id)
      .result.headOption
    }

    def get(): Future[Seq[EntityEmbeddedId]] =
      RunDBAction(TodoCategoryTable, "slave") { query =>
        query.result
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
