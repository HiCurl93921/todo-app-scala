/**
 *
 * to do sample project
 *
 */

package controllers

import javax.inject._
import play.api.mvc._
import models.{CreatingTodo, DisplayTodo, SelectableCategories, Todo, TodoCategory, EditingTodo, ViewValueHome}
import play.api.i18n.I18nSupport

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
    for {
      embeddedTodos <- todoRepository.get()
      embeddedCategories <- categoryRepository.get()
    } yield {
      Ok(views.html.Home(vv, createDisplayTodos(embeddedTodos, embeddedCategories),new SelectableCategories(embeddedCategories), creating))
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
        for {
          embeddedTodos <- todoRepository.get()
          embeddedCategories <- categoryRepository.get()
        } yield {
          BadRequest(views.html.Home(vv, createDisplayTodos(embeddedTodos, embeddedCategories), new SelectableCategories(embeddedCategories), errors))
        }
      },
      success => {
        createTodo(success) map { _ =>
          Redirect(routes.HomeController.index())
        }
      }
    )
  }

  def edit(id: Long) = Action.async { implicit request =>
    for {
      result <- todoRepository.get(Todo.Id(id))
      categories <- categoryRepository.get()
    } yield {
      Ok(views.html.EditTodo(vv.copy(title = "EDIT TODO"), id, EditingTodo.form(result.get), new SelectableCategories(categories)))
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    editing.bindFromRequest.fold(
      errors => {
        for {
          embeddedTodos <- todoRepository.get()
          embeddedCategories <- categoryRepository.get()
        } yield {
          BadRequest(views.html.Home(vv, createDisplayTodos(embeddedTodos, embeddedCategories), new SelectableCategories(embeddedCategories), creating))
        }
      },
      success => {
        updateTodo(id, success) map { _ =>
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


  private def createTodo(creatingTodo: CreatingTodo): Future[Seq[DisplayTodo]] = for {
    _ <- todoRepository.add(creatingTodo.to)
    todos <- todoRepository.get()
    categories <- categoryRepository.get()
  } yield { createDisplayTodos(todos, categories) }

  private def updateTodo(id: Long, edited: EditingTodo): Future[Option[Todo#EmbeddedId]] = todoRepository.update(edited.to(id))
}
