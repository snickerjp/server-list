package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import js.jquery.JQueryArtifacts
import sitemap._
import Loc._
import mapper._

import code.model._
import net.liftmodules.JQueryModule


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // テーブルおよびカラム名をアンダーバー区切りにする
    LiftRules.stripComments.default.set(() => false)
    MapperRules.columnName = (_,name) => Helpers.snakify(name)
    MapperRules.tableName = (_,name) => Helpers.snakify(name)

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
//    Schemifier.schemify(true, Schemifier.infoF _, User, ServerData, ServiceData)

    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
    def sitemap = SiteMap(
//      Menu(<span class="btn btn-default glyphicon glyphicon-list" />) / "index",
//      Menu(<span class="btn btn-default glyphicon glyphicon-plus" />) / "add",
      Menu.i("list") / "index",
/*      Menu.i("add") / "add",
      Menu.i("detail") / "detail" >> Hidden,
      Menu.i("edit") / "edit" >> Hidden,
      Menu.i("service") / "service"*/
      Menu.i("config") / "config"
      )

//    def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
// ユーザ登録はいらん
//    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))
    LiftRules.setSiteMapFunc(() => sitemap)

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery191
    JQueryModule.init()

    // CLASSPATHで指定可能にするフォルダ
    ResourceServer.allow {
      case "css" :: _ => true
      case "js" :: _ => true
      case "fonts" :: _ => true
      case "img" :: _ => true
    }
    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, ServerData, ServiceData)
  }
}
