package code.api.v1

import net.liftweb.http.S
import net.liftweb.common.{Full,Box, Empty}
import net.liftweb.mapper._
import scala.util.control.Exception._
import code.api.core.ApiTrait
import code.api.common._
import code.model._

/** サーバの情報を更新するAPI */
class UpdateServerData extends ApiTrait {
  val format = new java.text.SimpleDateFormat("yyyyMMdd")
  override def getParam() : Map[String, Box[String]] = {
    Map(
      "hostName" -> S.param("hostName"),
      "localIpAddress" -> S.param("localIpAddress"),

      "service" -> S.param("service"),
      "dataCenter" -> S.param("dataCenter"),
      "rackNumber" -> S.param("rackNumber"),
      "assetNumber" -> S.param("assetNumber"),
      "brandName" -> S.param("brandName"),
      "operatingSystem" -> S.param("operatingSystem"),
      "runningFlg" -> S.param("runningFlg"),
      "warrantyPeriod" -> S.param("warrantyPeriod"),
      "lastBackupDate" -> S.param("lastBackupDate"),
      "description" -> S.param("description"),
      "tags" -> S.param("tags")
    )
  }                                                                                     

  override def validate(params : Map[String, Box[String]]) : List[Map[String, String]] = {
    if (params.isEmpty) {
      ErrorCode.IllecalParams
    } else if (params("hostName").isEmpty) {
      ErrorCode.NothingHostName
    } else if (params("localIpAddress").isEmpty) {
      ErrorCode.NothingLocalIpAddress
    } else {
      ServerData.findAll(By(ServerData.hostName, params("hostName").open_!),
         BySql("local_ip_address like ?",
           IHaveValidatedThisSQL("dchenbecker", "2008-12-03"),
           "%" + params("localIpAddress").open_! + "%")
         ) match {
           case list if (list.size == 1) => Nil
           case _ => ErrorCode.NothingServerData
         }
    }
  }

  override def execute(params : Map[String, Box[String]]) = {
    val serverData = ServerData.findAll(By(ServerData.hostName, params("hostName").open_!),
      BySql("local_ip_address like ?",
      IHaveValidatedThisSQL("dchenbecker", "2008-12-03"),
        "%" + params("localIpAddress").open_! + "%")
      )(0)

    if (!params("service").isEmpty) {
      serverData.service(params("service").open_!.toLong)
    }
    if (!params("dataCenter").isEmpty) {
      serverData.dataCenter(params("dataCenter").open_!)
    }
    if (!params("rackNumber").isEmpty) {
      serverData.rackNumber(params("rackNumber").open_!)
    }
    if (!params("assetNumber").isEmpty) {
      serverData.assetNumber(params("assetNumber").open_!)
    }
    if (!params("brandName").isEmpty) {
      serverData.brandName(params("brandName").open_!)
    }
    if (!params("operatingSystem").isEmpty) {
      serverData.operatingSystem(params("operatingSystem").open_!)
    }
    if (!params("runningFlg").isEmpty) {
      serverData.runningFlg(params("runningFlg").open_!.toInt)
    }
    if (!params("warrantyPeriod").isEmpty) {
      serverData.warrantyPeriod(format.parse(params("warrantyPeriod").open_!))
    }
    if (!params("lastBackupDate").isEmpty) {
      serverData.lastBackupDate(format.parse(params("lastBackupDate").open_!))
    }
    if (!params("description").isEmpty) {
      serverData.description(params("description").open_!)
    }
    if (!params("tags").isEmpty) {
      serverData.tags(params("tags").open_!)
    }

    serverData.save

    Map("message" -> "成功しました", "serverData" -> Map(
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
    ))
  }

}
