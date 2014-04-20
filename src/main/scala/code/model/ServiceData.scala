package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http.SHtml
import net.liftweb.http.SHtml._
import net.liftweb.http.S

/**
 * The singleton that has methods for accessing the database
 */
object ServiceData extends ServiceData with LongKeyedMetaMapper[ServiceData] {
  override def fieldOrder = List(id, name, colorCode)
}

class ServiceData extends LongKeyedMapper[ServiceData] {

  def getSingleton = ServiceData
  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 50) {
    override def displayName = S.?("service")
  }
  object colorCode extends MappedString(this, 6) {
    override def displayName = S.?("colorCode")
  }
}
