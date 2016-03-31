package code.api.core

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json.JsonAST.{JString,JValue}
import scala.xml.Elem
import net.liftweb.common.Full
import code.api.common._

object BootRest extends RestHelper {
  val apiPackage = "code.api."
  serve {
    case Req(ver :: appName :: _, "xml", _) =>
      Full(
        try {
          val appInstance = this.getClass.getClassLoader.loadClass(apiPackage + ver + "." + appName).newInstance
          appInstance match {
            case app:ApiTrait => app.toXml
            case _ => Error501.toXml
          }
        } catch {
          case e:ClassNotFoundException => Error404.toXml
          case e:Exception => Error500.toXml
        }
      )
    case Req(ver :: appName :: _, "json", _) =>
      Full(
        try {
          val appInstance = this.getClass.getClassLoader.loadClass(apiPackage + ver + "." + appName).newInstance
          appInstance match {
            case app:ApiTrait => app.toJson
            case _ => Error501.toJson
          }
        } catch {
          case e:ClassNotFoundException => Error404.toJson
          case e:Exception => Error500.toJson
        }
      )
//    // Getは受け付けない
//    case Req(ver :: appName :: _, "xml", GetRequest) => Full(Error403.toXml)
//    case Req(ver :: appName :: _, "json", GetRequest) => Full(Error403.toJson)
  }
}
