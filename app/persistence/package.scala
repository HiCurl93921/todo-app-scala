import persistence.repositories.{TodoCategoryRepository, TodoCategoryRepositoryOnDataBase, TodoRepository, TodoRepositoryOnDataBase}

package object persistence {
  object onMySql {
    implicit lazy val driver = slick.jdbc.MySQLProfile
    object TodoRepository {
      val repository: TodoRepository[_] = TodoRepositoryOnDataBase()
    }
    object TodoCategoryRepository {
      val repository: TodoCategoryRepository[_] = TodoCategoryRepositoryOnDataBase()
    }
  }
}
