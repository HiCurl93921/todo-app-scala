package controllers

import models.services.CategoryService
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CategoryController @Inject() (cc: ControllerComponents)
  (implicit executionContext: ExecutionContext)
  extends AbstractController(cc) {

  private val categoryService: CategoryService = new CategoryService()

  def get(): Action[AnyContent] = Action.async {
    categoryService.get() map { response =>
      Ok(Json.toJson(response))
    }
  }
}
