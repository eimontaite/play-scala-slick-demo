package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class CityController @Inject()(repo: CityRepository,
                               cc: MessagesControllerComponents
                              )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val cityForm: Form[CreateCityForm] = Form {
    mapping(
      "name" -> nonEmptyText
    )(CreateCityForm.apply)(CreateCityForm.unapply)
  }

  private def citiesResult(form: Form[CreateCityForm])(implicit request: play.api.mvc.MessagesRequestHeader) = {
    for {
      cities <- repo.list()
    } yield {
      Ok(views.html.addCity(form, cities))
    }
  }

  /**
    * The index action.
    */
  def index = Action.async { implicit request =>
    citiesResult(cityForm)
  }

  /**
    * The add city action.
    *
    */
  def addCity = Action.async { implicit request =>
    cityForm.bindFromRequest.fold(
      errorForm => {
        citiesResult(errorForm)
      },
      city => {
        repo.create(city.name).map { _ =>
          Redirect(routes.PersonController.index).flashing("success" -> "City has been created")
        }
      }
    )
  }

  /**
    * A REST endpoint that gets all the cities as JSON.
    * //    */
  def getCities = Action.async { implicit request =>
    repo.list().map { cities =>
      Ok(Json.toJson(cities))
    }
  }

}

case class CreateCityForm(name: String) {
  def toModel(id: Long): City = {
    City(
      id,
      name
    )
  }
}

