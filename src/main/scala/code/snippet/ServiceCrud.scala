package code.snippet

import scala.xml.{NodeSeq, Group}
import net.liftweb.http.RequestVar
import net.liftweb.common.{Empty, Box, Full}
import code.model._
import net.liftweb.util.SecurityHelpers
import java.util.Date
import net.liftweb.http.SHtml
import scala.xml.Text
import net.liftweb.http.S
import net.liftweb.mapper._
import net.liftweb.util.Helpers._

class ServiceCrud {
  private object selectedService extends RequestVar[Box[ServiceData]](Empty)

  def serviceList(xhtml : NodeSeq) : NodeSeq = {
    ServiceData.findAll.flatMap(s => generateServiceHtmlLine(xhtml, s))
  }

  def generateServiceHtmlLine(xhtml : NodeSeq, sd : ServiceData) : NodeSeq = {
    bind("service", xhtml,
      "id" -> sd.id.get,
      "service" -> {
        val style = "padding-left : 7px;border-left : 7px solid #" + sd.colorCode.get + ";"
        <span style={style}>{sd.name.get}</span>
      },
      "runningflg" -> sd.runningFlg.asHtml,
      "edit" -> <blank />//editModal(sd)
    )
  }

  def add(): NodeSeq = {
    selectedService.is.openOr(ServiceData.create).toForm(Full(S.?("add")), "/config")
  }
}
