package controllers

import models.services.TodoService
import models.todos.{CreatingTodo, Todo, UpdatingTodo}
import play.api.libs.json.{JsError, JsString, JsSuccess, JsValue, Json}
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
    todoService.get() map { responses =>
      Ok(Json.toJson(responses))
    }
  }

  def getById(id: Long): Action[AnyContent] = Action.async {
    getByIdWithJson(id)
  }

  private def getByIdWithJson(id: Long): Future[Result] = todoService.get(Todo.Id(id)) map {
    case None => NotFound(JsString(""))
    case Some(response) => Ok(Json.toJson(response))
  }

  def add(): Action[JsValue] = Action(parse.json).async { request: Request[JsValue] =>
    request.body.validate[CreatingTodo] match {
      case JsSuccess(input, _) => todoService.add(input.to) flatMap { getByIdWithJson(_) }
      case JsError(errors) => Future.successful(BadRequest(JsError.toJson(errors)))
    }
  }

  def update(id: Long): Action[JsValue] = Action(parse.json).async { request: Request[JsValue] =>
    request.body.validate[UpdatingTodo] match {
      case JsSuccess(input, _) => todoService.update(input.to(id)) map {
        case None => BadRequest(Json.toJson(""))
        case Some(response) => Ok(Json.toJson(response))
      }
      case JsError(errors) => Future.successful(BadRequest(JsError.toJson(errors)))
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    todoService.delete(Todo.Id(id)) map {
      case None => BadRequest(Json.toJson(0))
      case Some(old) => Ok(Json.toJson(old.id))
    }
  }
}