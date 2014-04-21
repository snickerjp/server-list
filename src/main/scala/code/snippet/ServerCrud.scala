package code.snippet

import scala.xml.{NodeSeq, Group}
import net.liftweb.http.RequestVar
import net.liftweb.common.{Empty, Box, Full}
import code.model._
import net.liftweb.util.SecurityHelpers
import java.util.Date
import net.liftweb.http.SHtml
import net.liftweb.http.SHtml._
import scala.xml.Text
import net.liftweb.http.S
import net.liftweb.mapper._
import net.liftweb.util.Helpers._

class ServerCrud {
  private object selectedServer extends RequestVar[Box[ServerData]](Empty)

  def serverList(xhtml : NodeSeq) : NodeSeq = {
    val serverList = (S.param("service"), S.param("keyword")) match {
      case (Full(ser), Full(key)) if ser != "" => ServerData.findAll(BySql("concat(data_center, rack_number, asset_number, brand_name, operating_system, host_name, local_ip_address, tags) like ?", IHaveValidatedThisSQL("dchenbecker", "2008-12-03"), "%" + key + "%"), By(ServerData.service, ser.toInt))
      case (_, Full(key)) => ServerData.findAll(BySql("concat(data_center, rack_number, asset_number, brand_name, operating_system, host_name, local_ip_address, tags) like ?", IHaveValidatedThisSQL("dchenbecker", "2008-12-03"), "%" + key + "%"))
      case (Full(ser), _) => ServerData.findAll(By(ServerData.service, ser.toInt))
      case _ => ServerData.findAll
    }
    (S.param("runningFlg") match {
      case Full("1") => serverList.filter(_.runningFlg.get == 1)
      case _ => serverList
    }).flatMap(generateServerHtmlLine(xhtml, _))
  }

  def relateServerList(xhtml : NodeSeq) : NodeSeq = {
    (selectedServer.is match {
      case Full(sd) => ServerData.findAll(By(ServerData.brandName, sd.hostName.get)) :::
        ServerData.findAll(By(ServerData.hostName, sd.brandName.get))
      case _ => S.redirectTo("/index", () => ())
    }).flatMap(generateServerHtmlLine(xhtml, _))
  }

  def generateServerHtmlLine(xhtml : NodeSeq, sd : ServerData) : NodeSeq = {
    bind("server", xhtml,
      "id" -> sd.id.get,
      "service" -> sd.service.asHtml,
      "datacenter" -> sd.dataCenter.get,
      "racknumber" -> sd.rackNumber.get,
      "assetnumber" -> sd.assetNumber.get,
      "brandname" -> sd.brandName.get,
      "operatingsystem" -> sd.operatingSystem.get,
      "hostname" -> sd.hostName.get,
      "localipaddress" -> sd.localIpAddress.asHtml,
      "runningflg" -> sd.runningFlg.asHtml,
      "tags" -> sd.tags.asHtml,
      "detail" -> SHtml.link("/detail", () => selectedServer(Full(sd)), Text(S.?("detail")), "class" -> "btn btn-info btn-xs"),
      "edit" -> SHtml.link("/edit", () => selectedServer(Full(sd)), Text(S.?("edit")), "class" -> "btn btn-info btn-xs")
    )
  }
  def saveServerData(server: ServerData) : NodeSeq =  {
    server.validate match {
      case Nil => server.save; S.redirectTo("/detail", () => selectedServer(Full(server)))
      case x => S.error(x); selectedServer(Full(server)); <blank />
    }
  }
  def add(): NodeSeq = {
    selectedServer.is.openOr(ServerData.create).toForm(Full(S.?("add")), saveServerData _)
  }
  def edit(): NodeSeq = {
    selectedServer.is.openOr(ServerData.create).toForm(Full(S.?("edit")), saveServerData _)
  }
  def detail() : NodeSeq = {
    selectedServer.is match {
      case Full(server) => server.toHtml ++ <tr><td colspan="2" align="right">{SHtml.link("/edit", () => selectedServer(Full(server)), Text(S.?("edit")), "class" -> "btn btn-info")}</td></tr>
      case _ => S.redirectTo("/index", () => ())
    }
  }

  def keywordSearchForm(xhtml : NodeSeq) : NodeSeq = {
    def selectOption(s : ServiceData) : NodeSeq = S.param("service") match {
      case Full(id) if id == s.id.get.toString => <option value={s.id.get.toString} selected="on">{s.name.get}</option>
      case _ => <option value={s.id.get.toString}>{s.name.get}</option>
    }
    val serviceOptions : NodeSeq = ServiceData.findAll.flatMap(s => selectOption(s))
    val running : NodeSeq = S.param("runningFlg") match {
        case Full("1") => <xmlGroup><input type="checkbox" name="runningFlg" value="1" checked="on" /><span>{S.?("runningonly")}</span></xmlGroup>
        case _ => <xmlGroup><input type="checkbox" name="runningFlg" value="1" /><span>{S.?("runningonly")}</span></xmlGroup>
    }
    bind("searchform", xhtml,
      "service" -> <select name="service" class="form-control"><option value=""></option>{serviceOptions}</select>,
      "keyword" -> <input type="text" name="keyword" class="form-control" placeholder="Keyword Search" value={S.param("keyword").openOr("")} />,
      "running" -> {running}
    )
  }
}
