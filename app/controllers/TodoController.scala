package controllers

import com.sun.net.httpserver.Authenticator.Success
import models.categories.TodoCategory
import models.services.TodoService
import models.todos.{CreatingTodo, ResponseTodo, Todo}
import play.api.libs.json.{JsString, JsValue, Json, JsSuccess, JsError}
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
    todoService.get(ResponseTodo.apply) map { responses =>
      Ok(Json.toJson(responses))
    }
  }

  def getById(id: Long): Action[AnyContent] = Action.async {
    getByIdWithJson(id) map { Ok(_) }
  }

  private def getByIdWithJson(id: Long): Future[JsValue] = todoService.get(id, ResponseTodo.apply) map {
    case None => JsString("")
    case Some(response) => Json.toJson(response)
  }

  def add(): Action[JsValue] = Action(parse.json).async { request: Request[JsValue] =>
    request.body.validate[CreatingTodo] match {
      case JsSuccess(input, _) => todoService.add(input.to) flatMap { getByIdWithJson(_) map { Ok(_) } }
      case JsError(errors) => Future.successful(BadRequest(JsError.toJson(errors)))
    }
  }
}