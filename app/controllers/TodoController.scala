package controllers

import com.sun.net.httpserver.Authenticator.Success
import models.categories.TodoCategory
import models.services.TodoService
import models.todos.{ResponseTodo, Todo}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents}
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TodoController @Inject()(cc: ControllerComponents)
  (implicit executionContext: ExecutionContext)
  extends AbstractController(cc) {
  private val todoService: TodoService = new TodoService()

  def get(): Action[AnyContent] = Action.async {
    todoService.get(parseResponse) map { responses =>
      Ok(Json.toJson(responses))
    }
  }

  private def parseResponse(todos: Seq[Todo.EmbeddedId], categories: Seq[TodoCategory.EmbeddedId]): Seq[ResponseTodo] = for {
    todo <- todos
    category <- categories.find { _.id == todo.v.categoryId }
  } yield { ResponseTodo(todo, category) }
}