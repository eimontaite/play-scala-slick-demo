package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import views.html

import scala.concurrent.{ExecutionContext, Future}

class PersonController @Inject()(repo: PersonRepository,
                                 cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  /**
    * The mapping for the person form.
    */
  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "middleName" -> optional(text),
      "age" -> number.verifying(min(0), max(140))
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  private def personsResult(form: Form[CreatePersonForm])(implicit request: play.api.mvc.MessagesRequestHeader) = {
    for {
      persons <- repo.list()
    } yield {
      Ok(views.html.index(form, persons))
    }
  }

  /**
    * The index action.
    */
  def index = Action.async { implicit request =>
    personsResult(personForm)
  }

  /**
    * The add person action.
    *
    * This is asynchronous, since we're invoking the asynchronous methods on PersonRepository.
    */
  def addPerson = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle success.
    personForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        personsResult(errorForm)
      },
      // There were no errors in the form, so create the person.
      person => {
        repo.create(person.name, person.middleName, person.age).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.PersonController.index).flashing("success" -> "user.created")
        }
      }
    )
  }

  /**
    * A REST endpoint that gets all the people as JSON.
    * //    */
  def getPersons = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people))
    }
  }

  /**
    * Display the edit form
    *
    */
  def editPerson(id: Long) = Action.async { implicit request =>
    val persons = for {
      person <- repo.findById(id)
    } yield person

    persons.map {
      case person =>
        person match {
          case Some(p) => Ok(views.html.editForm(id, personForm.fill(CreatePersonForm(p.name, p.middleName, p.age))))
          case None => NotFound
        }
    }
  }

  /**
    * Handle the submitted edit form
    *
    */
  def update(id: Long) = Action.async { implicit request =>
    personForm.bindFromRequest.fold(
      errorForm => {
        personsResult(errorForm)
      },
      person => {
        repo.update(id, person).map { _ =>
          Redirect(routes.PersonController.index).flashing("success" -> "Person %s has been updated".format(person.name))
        }
      }
    )
  }
}

  /**
    * The create person form.
    *
    * Generally for forms, you should define separate objects to your models, since forms very often need to present data
    * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
    * that is generated once it's created.
    */
  case class CreatePersonForm(name: String, middleName: Option[String], age: Int)

