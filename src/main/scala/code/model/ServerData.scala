package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http.SHtml
import net.liftweb.http.SHtml._
import net.liftweb.http.S
import scala.xml.XML

/**
 * The singleton that has methods for accessing the database
 */
object ServerData extends ServerData with LongKeyedMetaMapper[ServerData] {
  override def fieldOrder = List(id, service, dataCenter, rackNumber, assetNumber, brandName, operatingSystem, hostName, localIpAddress, runningFlg, warrantyPeriod, lastBackupDate, description, tags)
}

class ServerData extends LongKeyedMapper[ServerData] {

  def getSingleton = ServerData
  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object service extends MappedLongForeignKey(this, ServiceData) {
    override def displayName = S.?("service")
    override def _toForm = {
      Full(
        SHtml.select(
          ServiceData.findAll.map(s => SelectableOption(s.id.get.toString, s.name.get)),
          Full(this.get.toString),
          f => set(f.toLong)
        )
      )
    }
    override def asHtml = {
      this.foreign match {
        case Full(serviceData) => {
          val style = "padding-left : 7px;border-left : 7px solid #" + serviceData.colorCode.get + ";"
          <span style={style}>{serviceData.name.get}</span>
        }
        case _ => <span>{this.get}</span>
      }
    }
  }
  object dataCenter extends MappedString(this, 50) {
    override def displayName = S.?("datacenter")
  }
  object rackNumber extends MappedString(this, 50) {
    override def displayName = S.?("racknumber")
  }

  object assetNumber extends MappedString(this, 50) {
    override def displayName = S.?("assetnumber")
  }
  object brandName extends MappedString(this, 50) {
    override def displayName = S.?("brandname")
  }
  object operatingSystem extends MappedString(this, 50) {
    override def displayName = S.?("os")
  }
  object hostName extends MappedString(this, 50) {
    override def displayName = S.?("hostname")
  }
  object localIpAddress extends MappedString(this, 255) {
    override def displayName = S.?("localip")
    override def asHtml = {
      if (this.get == null) <blank /> else <ol class="breadcrumb" style="margin:0;padding:0;">{this.get.split(",").flatMap(t => <li>{t.trim}</li>)}</ol>
    }
  }
  object runningFlg extends MappedInt(this) {
    override def displayName = S.?("runningflg")
    lazy val flgList = List(
      (5, S.?("building"),    "label label-primary"),
      (1, S.?("running"),     "label label-success"),
      (3, S.?("hotstandby"),  "label label-warning"),
      (4, S.?("coldstandby"), "label label-info"),
      (6, S.?("serviceout"),  "label label-warning"),
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

  object warrantyPeriod extends MappedDate(this) {
    override def displayName = S.?("warrantyperiod")
    override def asHtml = if (this.get == null) <blank /> else super.asHtml
  }
  object lastBackupDate extends MappedDate(this) {
    override def displayName = S.?("lastbackupdate")
    override def asHtml = if (this.get == null) <blank /> else super.asHtml
  }
  object description extends MappedTextarea(this, 1024) {
    override def textareaRows  = 5
    override def textareaCols = 50
    override def displayName = S.?("description")
    override def asHtml = {
      XML.loadString("<span>" + this.get.replaceAll("\n", "<br />") + "</span>")
    }
  }
  object tags extends MappedString(this, 255) {
    override def displayName = S.?("tags")
    override def asHtml = {
      if (this.get == null) <blank /> else <taggroup>{this.get.split(",").flatMap(t => <span class="badge">{t.trim}</span><span>&nbsp;</span>)}</taggroup>
    }
  }

}
