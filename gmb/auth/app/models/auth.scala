import play.api.libs.json._

package object auth {

   val STATUS_NOT_REGISTERED = 900;
   val STATUS_NOT_VALIDATED = 901;
   val STATUS_PASSWORD_INVALID = 902;

   // 1. Registering Engineer
   case class loginRequest(email:String, password: String)
   case class loginResponse(code: Int, token:String,role: String)

   case class registerRequest(email:String, password: String)
   case class registerResponse(code: Int, validationCode: String)

   case class validationRequest(email:String, validationCode: String)
   case class validationResponse(code: Int, token:String)

  implicit lazy val loginRequestReads: Reads[loginRequest] = Reads[loginRequest] {
     json => JsSuccess(loginRequest(
       (json \ "email").as[String],
       (json \ "password").as[String]
     ))
   }
  implicit lazy val loginResponseWrites: Writes[loginResponse] = Writes[loginResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "token" -> Json.toJson(o.token),
      "role" -> Json.toJson(o.role)
    ).filter(_._2 != JsNull))
  }

  implicit lazy val registerRequestReads: Reads[registerRequest] = Reads[registerRequest] {
     json => JsSuccess(registerRequest(
       (json \ "email").as[String],
       (json \ "password").as[String]
     ))
   }
  implicit lazy val registerResponseWrites: Writes[registerResponse] = Writes[registerResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "validationCode" -> Json.toJson(o.validationCode)
    ).filter(_._2 != JsNull))
  }

  implicit lazy val validationRequestReads: Reads[validationRequest] = Reads[validationRequest] {
     json => JsSuccess(validationRequest(
       (json \ "email").as[String],
       (json \ "validationCode").as[String]
     ))
   }
  implicit lazy val validationResponseWrites: Writes[validationResponse] = Writes[validationResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "token" -> Json.toJson(o.token)
    ).filter(_._2 != JsNull))
  }

  // Changing Role Service
  case class changeRoleRequest(code: Int, role: String, userid:String, token: String, changingUserid: String, changingUserCompany: String, newRole: String)
  case class changeRoleResponse(code: Int)

  implicit lazy val changeRoleRequestReads: Reads[changeRoleRequest] = Reads[changeRoleRequest] {
    json => JsSuccess(changeRoleRequest(
      (json \ "code").as[Int],
      (json \ "role").as[String],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "changingUserid").as[String],
      (json \ "changingUserCompany").as[String],
      (json \ "newRole").as[String]
    ))
  }
  implicit lazy val changeRoleResponseWrites: Writes[changeRoleResponse] = Writes[changeRoleResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code)
    ).filter(_._2 != JsNull))
  }

  // Changing Password Service
  case class changePasswordRequest(code: Int, userid:String, token: String, newPassword: String)
  case class changePasswordResponse(code: Int)

  implicit lazy val changePasswordRequestReads: Reads[changePasswordRequest] = Reads[changePasswordRequest] {
    json => JsSuccess(changePasswordRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "newPassword").as[String]
    ))
  }
  implicit lazy val changePasswordResponseWrites: Writes[changePasswordResponse] = Writes[changePasswordResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code)
    ).filter(_._2 != JsNull))
  }

  // Changing Password Service
  case class removeAccountRequest(code: Int, userid:String, token: String, deletedUserid: String)
  case class removeAccountResponse(code: Int)

  implicit lazy val removeAccountRequestReads: Reads[removeAccountRequest] = Reads[removeAccountRequest] {
    json => JsSuccess(removeAccountRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "deletedUserid").as[String]
    ))
  }
  implicit lazy val removeAccountResponseWrites: Writes[removeAccountResponse] = Writes[removeAccountResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code)
    ).filter(_._2 != JsNull))
  }

  // Google Apps Authenticate
  case class googleAppsAuthenticateRequest(code: String, authuser: String, session_state: String, prompt: String)
  case class googleAppsAuthenticateResponse(code: Int, email:String, accessToken: String, role: String)

  implicit lazy val googleAppsAuthenticateRequestReads: Reads[googleAppsAuthenticateRequest] = Reads[googleAppsAuthenticateRequest] {
    json => JsSuccess(googleAppsAuthenticateRequest(
      (json \ "code").as[String],
      (json \ "authuser").as[String],
      (json \ "session_state").as[String],
      (json \ "prompt").as[String]
    ))
  }
  implicit lazy val googleAppsAuthenticateWrites: Writes[googleAppsAuthenticateResponse] = Writes[googleAppsAuthenticateResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "email" -> Json.toJson(o.email),
      "token" -> Json.toJson(o.accessToken),
      "role" -> Json.toJson(o.role)
    ).filter(_._2 != JsNull))
  }

  // Facebook Apps Authenticate
  case class facebookAppsAuthenticateRequest(code: String)
  case class facebookAppsAuthenticateResponse(code: Int, email:String, accessToken: String,role: String)

  implicit lazy val facebookAppsAuthenticateRequestReads: Reads[facebookAppsAuthenticateRequest] = Reads[facebookAppsAuthenticateRequest] {
    json => JsSuccess(facebookAppsAuthenticateRequest(
      (json \ "code").as[String]
    ))
  }
  implicit lazy val facebookAppsAuthenticateWrites: Writes[facebookAppsAuthenticateResponse] = Writes[facebookAppsAuthenticateResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "email" -> Json.toJson(o.email),
      "token" -> Json.toJson(o.accessToken),
      "role" -> Json.toJson(o.role)
    ).filter(_._2 != JsNull))
  }

  // Changing Password Service
  case class registerCompanyRequest(code: Int, userid:String, token: String, companycode: String, companyname: String)
  case class registerCompanyResponse(code: Int, registerationSuccessCode: Int)

  implicit lazy val registerCompanyRequestReads: Reads[registerCompanyRequest] = Reads[registerCompanyRequest] {
    json => JsSuccess(registerCompanyRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companycode").as[String],
      (json \ "companyname").as[String]
    ))
  }
  implicit lazy val registerCompanyResponseWrites: Writes[registerCompanyResponse] = Writes[registerCompanyResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "registerationSuccessCode" -> Json.toJson(o.registerationSuccessCode)
    ).filter(_._2 != JsNull))
  }

}
