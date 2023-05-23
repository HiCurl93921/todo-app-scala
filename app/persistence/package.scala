package object persistence {
  val default = onMySql

  object onMySql {
    implicit lazy val driver = slick.jdbc.MySQLProfile

    object TodoRepository extends repositories.TodoRepository
    object TodoCategoryRepository extends repositories.TodoCategoryRepository
  }
}
