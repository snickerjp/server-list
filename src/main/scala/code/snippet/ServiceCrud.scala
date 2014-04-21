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
    <tr>{ServiceData.htmlHeaders}</tr> ++
      ServiceData.findAll.flatMap(s => <tr>{s.htmlLine}</tr>)
  }

  def saveServiceData(service: ServiceData) : NodeSeq =  {
    service.validate match {
      case Nil => service.save; S.redirectTo("/service", () => selectedService(Empty))
      case x => S.error(x); selectedService(Full(service)); <blank />
    }
  }
  def add(): NodeSeq = {
    selectedService.is.openOr(ServiceData.create).toForm(Full(S.?("add")), saveServiceData _)
  }
}
