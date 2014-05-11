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
  override def fieldOrder = List(id, name, colorCode, runningFlg)
}

class ServiceData extends LongKeyedMapper[ServiceData] {

  def getSingleton = ServiceData
  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 50) {
    override def displayName = S.?("service")
  }
  object colorCode extends MappedString(this, 6) {
    override def displayName = S.?("colorcode")
  }
  object runningFlg extends MappedInt(this) {
    override def displayName = S.?("runningflg")
    lazy val flgList = List(
      (1, S.?("running"),     "label label-success"),
      (2, S.?("stop"),        "label label-default")
    )
    override def _toForm = {
      Full(
        SHtml.select(
          flgList.map(a => SelectableOption(a._1.toString, a._2)),
          Full(this.get.toString),
          f => set(f.toInt)
        )
      )
    }
    override def asHtml = {
      flgList.filter(_._1 == this.get) match {
        case Nil => <span>{this.get}</span>
        case a => <span class={a(0)._3}>{a(0)._2}</span>
      }
    }
  }
}
