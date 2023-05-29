package persistence.db

import ixias.persistence.model.{DataSourceName, Table}
import models.todos.Todo
import models.categories.TodoCategory
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.time.LocalDateTime
case class TodoTable [P <: JdbcProfile]()(implicit val driver: P)
  extends Table[Todo, P] {
  import api._

  lazy val dsn   : Map[String, DataSourceName] = Map (
    "master"  -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave"   -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  class Table(tag: Tag) extends BasicTable(tag, "to_do") {
    import models.todos.Todo._

    def id = column[Todo.Id] ("id", O.UInt64, O.PrimaryKey, O.AutoInc)
    def categoryId = column[TodoCategory.Id]("category_id", O.UInt64)
    def title = column[String] ("title", O.Utf8Char255)
    def body = column[String] ("body",  O.Text)
    def state = column[Short] ("state", O.UInt8)
    def updatedAt = column[LocalDateTime]("updated_at", O.TsCurrent)
    def createdAt = column[LocalDateTime]("created_at", O.Ts)

    type TableElementTuple = (
      Option[Id], TodoCategory.Id, String, String, Short, LocalDateTime, LocalDateTime
    )

    def * : ProvenShape[Record] = (id.?, categoryId, title, body, state, updatedAt, createdAt) <> (
      (t: TableElementTuple) => Todo(
        t._1, t._2, t._3, t._4, t._5, t._6, t._7
      ),
      (v: TableElementType) => Todo.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, t._5, t._6, t._7
      )}
    )
  }
}
