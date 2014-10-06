package code.rest

import net.liftweb.http._
import net.liftweb.http.rest._
import code.model._

object FileDownloadRest extends RestHelper {
  serve {
    case Req("api" :: "download" :: _, "csv", GetRequest) => FileDownloadRest.csvFile
  }

  def csvFile : PlainTextResponse = {
    PlainTextResponse(ServerData.findAll().flatMap(r => r.id.get + "," + r.hostName.get).toString)
  }
}
