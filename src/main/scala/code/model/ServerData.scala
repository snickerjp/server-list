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
object ServerData extends ServerData with LongKeyedMetaMapper[ServerData] {
  override def fieldOrder = List(id, service, dataCenter, rackNumber, assetNumber, brandName, operatingSystem, hostName, localIpAddress, serverType, roll, runningFlg, description, tags)
}

class ServerData extends LongKeyedMapper[ServerData] {

  def getSingleton = ServerData
  def primaryKeyField = id

  object id extends MappedLongIndex(this)
  object service extends MappedLongForeignKey(this, ServiceData) {
    override def displayName = S.?("service")
    override def _toForm = {
      // ServiceList.findAll.map(s => SelecteableOption(s.id.get, s.name.get))
      Full(
        SHtml.select(
          ServiceData.findAll.map(s => SelectableOption(s.id.get.toString, s.name.get)),
          Full(is.toString),
          f => set(f.toLong)
        )
      )
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
  object localIpAddress extends MappedString(this, 50) {
    override def displayName = S.?("localip")
  }
  object serverType extends MappedString(this, 8) {
    override def displayName = S.?("servertype")
  }
  object roll extends MappedString(this, 255) {
    override def displayName = S.?("roll")
  }
  object runningFlg extends MappedBoolean(this) {
    override def displayName = S.?("runningflg")
  }
  object description extends MappedTextarea(this, 1024) {
    override def textareaRows  = 5
    override def textareaCols = 50
    override def displayName = S.?("discription")
  }
  object tags extends MappedString(this, 255) {
    override def displayName = S.?("tags")
  }

}
