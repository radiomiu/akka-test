package com.example

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCodes, _}
import akka.util.Timeout

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Router()(implicit val system: ActorSystem[_]) {

  private implicit val timeout: Timeout = Timeout.create(Config.timeout)
  private val mapboxUrl: String = Config.mapboxUrl
  private val mapboxToken: String = Config.mapboxToken

  def request(z: Int, x: Int, y: Int): Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = mapboxUrl + z + "/" + x + "/" + y + ".jpg?" + mapboxToken))

  val tileRoute: Route =
    pathPrefix("maps") {
      concat(
        path(IntNumber / IntNumber / IntNumber) { (x, y, z) =>
          get {
            TokenAuthorization.authenticated { _ =>
              implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
              onComplete(request(x, y, z)) {
                case Success(resp) ⇒ {
                  println(s"resp: $resp")
                  complete(resp)
                }
                case Failure(ex) => complete(StatusCodes.InternalServerError.intValue, ex.getMessage)
              }
            }
          }
        })
    }
}
