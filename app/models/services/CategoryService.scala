package models.services

import models.categories.ResponseCategory

import scala.concurrent.{ExecutionContext, Future}

class CategoryService (implicit executionContext: ExecutionContext) {
  private val categoryRepository = persistence.onMySql.TodoCategoryRepository.repository

  def get(): Future[Seq[ResponseCategory]] = categoryRepository.get() map {
    ResponseCategory(_)
  }
}
