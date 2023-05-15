package controllers

import models.Todo.toJson
import models.{TodoId, TodoInput, TodoUpdate}
import persistence.TodoRepository
import play.api.mvc.{AbstractController, ControllerComponents, Request, Result}
import play.api.libs.json._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TodoController @Inject() (todoRepository: TodoRepository, controllerComponents: ControllerComponents)
                               (implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) {
  def all() = Action.async {
    todoRepository.all() map { todos =>
      Ok(todos map {toJson} mkString(","))
    }
  }

  def findById(id: Long) = Action.async {
    todoRepository.findById(TodoId(id)) map { maybeTodo =>
      maybeTodo.fold[Result](NotFound)(todo =>
        Ok(toJson(todo))
      )
    }
  }

  def create() = Action(parse.json).async { request: Request[JsValue] =>
    val inputRequest: JsResult[TodoInput] = request.body.validate[TodoInput]
    inputRequest match {
      case JsSuccess(input: TodoInput, _: JsPath) => {
        todoRepository.create(input) map {
          result => Ok(toJson(result))
        } recover { case ex => BadRequest(ex.getMessage) }
      }
      case JsError(errors: Seq[(JsPath, Seq[JsonValidationError])]) => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      }
    }
  }

  def update(id: Long) = Action(parse.json).async { request: Request[JsValue] =>
    val inputRequest: JsResult[TodoUpdate] = request.body.validate[TodoUpdate]
    inputRequest match {
      case JsSuccess(update: TodoUpdate, _: JsPath) => {
        todoRepository.update(TodoId(id), update) map {
          result => Ok(toJson(result))
        } recover { case ex => BadRequest(ex.getMessage) }
      }
      case JsError(errors: Seq[(JsPath, Seq[JsonValidationError])]) => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      }
    }
  }

  def delete(id: Long) = Action.async {
    todoRepository.delete(TodoId(id)) map { result =>
      Ok(result.toString)
    }
  }
}
