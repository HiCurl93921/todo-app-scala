package controllers

import javax.inject._
import play.api.mvc._
import play.api.data.Form
import models.categories.{SelectableCategories, TodoCategory}
import models.todos.{CreatingTodo, DisplayTodo, EditingTodo, Todo}
import models.ViewValueHome
import play.api.i18n.I18nSupport
import play.twirl.api.HtmlFormat

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(cc: ControllerComponents)
(implicit executionContext: ExecutionContext)
  extends AbstractController(cc)
    with I18nSupport {

  private val creating = CreatingTodo.form
  private val editing = EditingTodo.form

  val vv = ViewValueHome(
    title = "Home",
    cssSrc = Seq("main.css"),
    jsSrc = Seq("main.js")
  )

  private val todoRepository     = persistence.onMySql.TodoRepository.repository
  private val categoryRepository = persistence.onMySql.TodoCategoryRepository.repository

  def index () = Action.async { implicit request =>
    showIndex(creating)(Ok.apply)
  }

  private def showIndex (form: Form[CreatingTodo])(res: HtmlFormat.Appendable => Result)(implicit request: RequestHeader) = {
    val todosRequest      = todoRepository.get()
    val categoriesRequest = categoryRepository.get()

    for {
      embeddedTodos <- todosRequest
      embeddedCategories <- categoriesRequest
    } yield {
      res(views.html.Home(vv, createDisplayTodos(embeddedTodos, embeddedCategories), new SelectableCategories(embeddedCategories), form))
    }
  }

  private def createDisplayTodos (todos: Seq[Todo.EmbeddedId], categories: Seq[TodoCategory.EmbeddedId]): Seq[DisplayTodo] = for {
    todo <- todos
    category <- categories.find(_.id == todo.v.categoryId)
  } yield {
    DisplayTodo(todo, category)
  }

  def create = Action.async { implicit request =>
    creating.bindFromRequest.fold(
      errors => {
        showIndex(errors)(BadRequest.apply)
      },
      success => {
        todoRepository.add(success.to) map { _ =>
          Redirect(routes.HomeController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async { implicit request =>
    val todoRequest = todoRepository.get(Todo.Id(id))
    val categoriesRequest = categoryRepository.get()

    for {
      result <- todoRequest
      categories <- categoriesRequest
    } yield {
      Ok(views.html.EditTodo(vv.copy(title = "EDIT TODO"), id, EditingTodo.form(result.get), new SelectableCategories(categories)))
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    editing.bindFromRequest.fold(
      errors => {
        showIndex(creating)(BadRequest.apply)
      },
      success => {
        todoRepository.update(success.to(id)) map { _ =>
          Redirect(routes.HomeController.index())
        }
      }
    )
  }

  def delete(id: Long) = Action.async { implicit request =>
    for {
      _ <- todoRepository.remove(Todo.Id(id))
    } yield { Redirect(routes.HomeController.index()) }
  }
}
