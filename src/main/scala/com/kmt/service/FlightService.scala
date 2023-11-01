package com.kmt.service

import cats.effect._
import com.kmt.entity.Flight

import java.io.File

object FlightService {

  private val FlightsCsvFileName = "flights.csv"

  private def dynamicFlightsDB = {
    val path = new File(s"/home/z/code/scala/fareplace-flight-service/src/main/resources/$FlightsCsvFileName")
      .toPath
    DynamicFlightsDB(path)
  }

  def flightExists(flight: Flight): IO[Boolean] = {
    dynamicFlightsDB.flightExists(flight)
  }

}

