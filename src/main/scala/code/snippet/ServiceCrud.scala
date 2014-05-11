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
    ServiceData.findAll(OrderBy(ServiceData.id, Ascending)).flatMap(s => generateServiceHtmlLine(xhtml, s))
  }

  def generateServiceHtmlLine(xhtml : NodeSeq, sd : ServiceData) : NodeSeq = {
    bind("service", xhtml,
      "id" -> sd.id.get,
      "service" -> {
        val style = "padding-left : 7px;border-left : 7px solid #" + sd.colorCode.get + ";"
        <span style={style}>{sd.name.get}</span>
      },
      "runningflg" -> sd.runningFlg.asHtml,
      "edit" -> editModal(sd)
    )
  }

  def editModal(sd : ServiceData) : NodeSeq = {
    <button class="btn btn-info btn-xs" data-toggle="modal" data-target={".sd-edit" + sd.id.get}>{S.?("edit")}</button>
    <div class={"modal fade sd-edit" + sd.id.get} tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <form method="post" action="/config">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
              <h4 class="modal-title">{sd.name.get}</h4>
            </div>
            <div class="modal-body">
              <table class="table table-border table-hover table-condensed">
                {sd.toForm(Empty, "/config")}
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">{S.?("close")}</button>
              <button type="submit" class="btn btn-primary">{S.?("edit")}</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  }

  def add(): NodeSeq = {
    selectedService.is.openOr(ServiceData.create).toForm(Full(S.?("add")), "/config")
  }
}
