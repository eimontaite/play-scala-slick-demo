package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * A repository for cities.
  *
  */
@Singleton
class CityRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class CityTable(tag: Tag) extends Table[City](tag, "cities") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    def * = (id, name) <> ((City.apply _).tupled, City.unapply)
  }

  private val cities = TableQuery[CityTable]

  def create(name: String): Future[City] = db.run {
    (cities.map(c => c.name)
      returning cities.map(_.id)
      into ((cityName, id) => City(id, cityName))
      ) += (name)
  }

  /**
    * Get city by ID
    */
  def findById(id: Long) =
    db.run(cities.filter(_.id === id).result.headOption)

  /**
    * Update a city.
    */
  def update(id: Long, city: City): Future[Int] = {
    db.run(cities.filter(_.id === id).update(city))
  }

  /**
    * Delete a city
    */
  def delete(id: Long) = {
    db.run(cities.filter(_.id === id).delete)
  }

  /**
    * List all the cities in the database.
    */
  def list(): Future[Seq[City]] = db.run {
    cities.result
  }

  /**
    * List city options
    *
    */

  //  def options() = {
  //    list().map(city => city.id.toString -> city.name)
  //  }

}



