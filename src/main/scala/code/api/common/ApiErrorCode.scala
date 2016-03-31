package code.api.common

import code.api.core.ApiErrorTrait

object Error403 extends ApiErrorTrait {
  override def errorCode = 403
  override def errorMessage = "アクセス権限がねーです。"
}
object Error404 extends ApiErrorTrait {
  override def errorCode = 404
  override def errorMessage = "そんなAPIはないです。"
}
object Error500 extends ApiErrorTrait {
  override def errorCode = 500
  override def errorMessage = "なんかエラーが起きました。"
}
object Error501 extends ApiErrorTrait {
  override def errorCode = 501
  override def errorMessage = "それはAPIではないです。"
}

object ErrorCode {
  val NothingHostName = List(Map("code" -> "100", "message" -> "hostNameが未指定です。"))
  val NothingLocalIpAddress = List(Map("code" -> "101", "message" -> "localIpAddressが未指定です。"))
  val IllecalParams = List(Map("code" -> "102", "message" -> "paramsが不正です。"))
  val NothingServerData  = List(Map("code" -> "102", "message" -> "該当するサーバ情報がありません。"))
}
