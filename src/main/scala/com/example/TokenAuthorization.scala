package com.example

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, headerValueByName, provide}
import pdi.jwt._

object TokenAuthorization {
  private val secretKey = Config.secret
  private val tokenExpiryPeriodInDays = 1

  def authenticated: Directive1[Map[String, Any]] = {

    headerValueByName("Authorization").flatMap { tokenFromUser =>
      println(tokenFromUser)
      println(Jwt.validate(tokenFromUser, secretKey, Seq(JwtAlgorithm.HS256)))
      tokenFromUser match {
        case token if Jwt.isValid(token, secretKey, Seq(JwtAlgorithm.HS256)) =>
          provide(Map.empty[String, String])
        case _ =>  complete(StatusCodes.Unauthorized ->"Invalid Token")
      }
    }
  }
}