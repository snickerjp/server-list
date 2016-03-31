package code.api.v1

import net.liftweb.http.S
import net.liftweb.common.{Full,Box, Empty}
import net.liftweb.mapper._
import scala.util.control.Exception._
import code.api.core.ApiTrait
import code.api.common._
import code.model._

/** サーバの情報を取得するAPI */
class GetServerData extends ApiTrait {
  override def getParam() : Map[String, Box[String]] = {
    Map(
      "keyword" -> S.param("keyword"),
      "service" -> S.param("service"),
      "runningOnly" -> S.param("runningOnly")
    )
  }                                                                                     

  override def execute(params : Map[String, Box[String]]) = {
    Map("message" -> "成功しました。",
      "list" -> 
      runningFilter(serviceFilter(keywordSearchList(params), params), params).map(generateMap)
    )
  }

  override def validate(params : Map[String, Box[String]]) : List[Map[String, String]] = Nil

  def keywordSearchList(params : Map[String, Box[String]]) : List[ServerData] = {
    params("keyword") match {
      case Full(key) => ServerData.findAll(
        BySql("lower(concat(data_center, rack_number, asset_number, brand_name, operating_system, host_name, local_ip_address, tags)) like lower(?)", IHaveValidatedThisSQL("dchenbecker", "2008-12-03"), "%" + key + "%")
      )
      case _ => ServerData.findAll
    }
  }
  def serviceFilter(serverList: List[ServerData], params : Map[String, Box[String]]) : List[ServerData] = {
    params("service") match {
      case Full(ser) if ser != "" => serverList.filter(_.service.get.toString == ser)
      case _ => serverList
    }
  }
  def runningFilter(serverList: List[ServerData], params : Map[String, Box[String]]) : List[ServerData] = {
    params("runningOnly") match {
      case Full("1") => serverList.filter(_.runningFlg.get == 1)
      case _ => serverList
    }
  }
  def generateMap(serverData : ServerData) : Map[String, Any] = {
    Map(
      "id" -> serverData.id.get,           
      "service" -> serverData.service.get,
      "dataCenter" -> serverData.dataCenter.get,
      "rackNumber" -> serverData.rackNumber.get,
      "assetNumber" -> serverData.assetNumber.get,
      "brandName" -> serverData.brandName.get,
      "operatingSystem" -> serverData.operatingSystem.get,
      "hostName" -> serverData.hostName.get,
      "localIpAddress" -> serverData.localIpAddress.get,
      "runningFlg" -> serverData.runningFlg.get,
      "warrantyPeriod" -> serverData.warrantyPeriod.get,
      "lastBackupDate" -> serverData.lastBackupDate.get,
      "description" -> serverData.description.get,
      "tags" -> serverData.tags.get
    )
  }

}
