package persistence.db

import ixias.persistence.model.{DataSourceName, Table}
import models.TodoCategory
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.time.LocalDateTime


case class TodoCategoryTable[P <: JdbcProfile]()(implicit val driver: P)
  extends Table[TodoCategory, P] {
  import api._

  lazy val dsn: Map[String, DataSourceName] = Map (
    "master" -> DataSourceName("ixias.db.mysql://master/to_do_category"),
    "slave" -> DataSourceName("ixias.db.mysql://slave/to_do_category")
  )

  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  class Table(tag: Tag) extends BasicTable(tag, "to_do_category") {
    import TodoCategory._

    def id = column[TodoCategory.Id]("id", O.UInt64, O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Utf8Char255)
    def slug = column[String]("slug", O.AsciiChar64)
    def color = column[Short]("color", O.UInt8)
    def updatedAt = column[LocalDateTime]("updated_at", O.TsCurrent)
    def createdAt = column[LocalDateTime]("created_at", O.Ts)

    type TableElementTuple = (
      Option[Id], String, String, Short, LocalDateTime, LocalDateTime
    )

    override def * : ProvenShape[Record] = (id.?, name, slug, color, updatedAt, createdAt) <> (
      (t: TableElementTuple) => TodoCategory(
        t._1, t._2, t._3, t._4, t._5, t._6
      ),
      (v: TableElementType) => TodoCategory.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, t._5, t._6
      )}
    )
  }
}
