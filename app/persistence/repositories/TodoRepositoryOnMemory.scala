//package persistence
//
//import javax.inject.Singleton
//import java.time.LocalDateTime
//import models.{Todo, TodoId, TodoInput, TodoUpdate}
//import org.joda.time.IllegalInstantException
//import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.language.postfixOps
//import scala.util.{Failure, Success, Try}
//
//class TodoRepositoryOnmMemoryImpl() extends TodoRepository {
////    private val todos: OnMemoryTodos = new OnMemoryTodos()
//
////    def all(): Future[Seq[Todo]] = Future { todos.all() }
////    def findById(todoId: TodoId): Future[Option[Todo]] = Future { todos.find(todoId) }
////    def create(input: TodoInput): Future[Todo] = Future {
////        val todo = createTodo(input)
////
////        todos.add(todo) fold(ex => throw ex, identity)
////    }
////
////    def update(id: TodoId, update: TodoUpdate): Future[Todo] = Future {
////        todos.update(id, update) fold(ex => throw ex, identity)
////    }
////
////    def delete(id: TodoId): Future[Unit] = Future { todos.delete(id) }
////
////    private def createTodo(input: TodoInput): Todo = {
////        val current = currentDateTime()
////        Todo(id = todos.nextId, title = input.title, body = input.body.getOrElse(""), createdAt = current, updatedAt = current)
////    }
////
////    private def currentDateTime() = LocalDateTime.now()
//
//    override def get (id: Id): Future[Option[EntityEmbeddedId]] = ???
//
//    override def add (entity: EntityWithNoId): Future[Id] = ???
//
//    override def update (entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] = ???
//
//    override def remove (id: Id): Future[Option[EntityEmbeddedId]] = ???
//
//    override type Database = this.type
//    override type Backend = this.type
//    override protected val backend: TodoRepositoryOnmMemoryImpl.this.type = ???
//    override           val api    : API     = ???
//}
//
//private object OnMemoryTodos {
//    private var todos: Seq[Todo] = Seq(
//        Todo(title = "todo1", body = "").
//    )
//
//    def nextId: TodoId = {
//        val maxId = todos map { todo => todo.id.value } max
//
//        TodoId(maxId + 1)
//    }
//
//    def exists(todo: Todo): Boolean = exists((todo.id))
//
//    def exists(id: TodoId): Boolean = todos.exists((_.id == id))
//
//    private def currentDateTime () = LocalDateTime.now()
//}