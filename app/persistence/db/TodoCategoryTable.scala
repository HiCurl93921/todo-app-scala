package persistence.db

import ixias.persistence.model.{DataSourceName, Table}
import models.categories.TodoCategory
import models.Color
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import java.time.LocalDateTime


case class TodoCategoryTable[P <: JdbcProfile]()(implicit val driver: P)
  extends Table[TodoCategory, P] {
  import api._

  lazy val dsn: Map[String, DataSourceName] = Map (
    "master" -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave" -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  class Table(tag: Tag) extends BasicTable(tag, "to_do_category") {
    import models.categories.TodoCategory._

    def id = column[TodoCategory.Id]("id", O.UInt64, O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Utf8Char255)
    def slug = column[String]("slug", O.AsciiChar64)
    def color = column[String]("color", O.Utf8Char64)
    def updatedAt = column[LocalDateTime]("updated_at", O.TsCurrent)
    def createdAt = column[LocalDateTime]("created_at", O.Ts)

    type TableElementTuple = (
      Option[Id], String, String, String, LocalDateTime, LocalDateTime
    )

    override def * : ProvenShape[Record] = (id.?, name, slug, color, updatedAt, createdAt) <> (
      (t: TableElementTuple) => TodoCategory(
        t._1, t._2, t._3, Color(t._4), t._5, t._6
      ),
      (v: TableElementType) => TodoCategory.unapply(v).map { t => (
        t._1, t._2, t._3, t._4.code, t._5, t._6
      )}
    )
  }
}
