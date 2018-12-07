package code.api.core

import scala.xml._
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Extraction
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.Xml
import net.liftweb.common.Box
import code.api.common._

trait ApiTrait {
  final def toXml: Node = {
    Xml.toXml(toJson).apply(0)
  }
  def toJson: JValue = {
    implicit val formats = DefaultFormats
    Extraction.decompose(run)
  }
  def checkBlackList : Boolean = true
  def execute(params : Map[String, Box[String]]) : Map[String, Any]
  def validate(params : Map[String, Box[String]]) : List[Map[String, String]]
  def getParam : Map[String, Box[String]]

  def run : Map[String, Any] = {
    if (!checkBlackList) Error403.run else {
      val params = getParam
      val errors = validate(params)
      if (!errors.isEmpty) {
        Map("resultSet" -> Map("statusCode" -> 400, "errors" -> errors))
      } else {
        try {
          Map("resultSet" -> Map("statusCode" -> 200, "result" -> execute(params)))
        } catch {
          case e : Exception => e.printStackTrace; Error500.run
        }
      }
    }
  }
}

trait ApiErrorTrait extends ApiTrait {
  def errorCode : Int = 0;
  def errorMessage : String = "";
  override def execute(params : Map[String, Box[String]]) : Map[String, Any] = Map.empty
  override def validate(params : Map[String, Box[String]]) : List[Map[String, String]] = Nil
  override def getParam : Map[String, Box[String]] = Map.empty

  override def run = {
    Map("resultSet" -> Map("statusCode" -> errorCode, "errors" -> Map("code" -> errorCode, "message" -> errorMessage)))
  }
}
