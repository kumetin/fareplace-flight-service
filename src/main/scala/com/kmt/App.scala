package com.kmt

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe.CirceInstances
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.blaze.server.BlazeServerBuilder
import com.kmt.entity.Flight
import com.kmt.service.FlightService
import io.circe.Json
import io.circe.syntax.EncoderOps

object App extends IOApp with CirceInstances {

  // Entity decoder for Flight
  implicit val flightEntityDecoder: EntityDecoder[IO, Flight] =
    jsonOf[IO, Flight]

  def routes(): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "fareplace" / "flightExists" =>
        req.decode[Flight] { flight =>
          FlightService.flightExists(flight).flatMap { exists =>
            Ok(exists.asJson)
          }
        }
    }

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
    BlazeServerBuilder[IO]
      .bindHttp(8082, "0.0.0.0")
      .withHttpApp(routes().orNotFound)
      .resource
      .useForever
      .as(ExitCode.Success)
  }
}
