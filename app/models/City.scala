package models

import play.api.libs.json._

case class City(id: Long, name: String)

object City {

  implicit val cityFormat = Json.format[City]

//
//  /**
//    * Parse a City from a ResultSet
//    */
//  val simple = {
//    get[Pk[Long]]("city.id") ~
//      get[String]("city.name") map {
//      case id~name => City(id, name)
//    }
//  }
//
//  /**
//    * Construct the Map[String,String] needed to fill a select options set.
//    */
//  def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
//    SQL("select * from company order by name").as(City.simple *).map(c => c.id.toString -> c.name)
//  }

}