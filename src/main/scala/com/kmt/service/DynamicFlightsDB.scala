package com.kmt.service

import cats.effect._
import com.kmt.entity.Flight
import com.kmt.service.DynamicFlightsDB.FlightReloader
import fs2._
import org.log4s.{Logger, getLogger}

import java.nio.file.{Path => JPath}
import scala.concurrent.duration.{DurationInt, FiniteDuration}

class DynamicFlightsDB(flightsCsvPath: JPath) {

  private val flightsStream: Stream[IO, Seq[Flight]] =
    FlightReloader.reloadFlightsFile(flightsCsvPath, 2.seconds)

  def flightExists(flight: Flight) = {
    flightsStream
      .flatMap(Stream.emits)
      .map(_.equals(flight))
      .exists(_ == true)
      .compile.last
      .map(_.getOrElse(false))
  }

}
object DynamicFlightsDB {

  private val logger = getLogger(getClass)

  def apply(flightsCsv: JPath) = {
    new DynamicFlightsDB(flightsCsv)
  }

  private def readFlightsFile(filePath: JPath): IO[List[Flight]] = {
    logger.info("Reading flights file")
    Stream
      .eval(IO(scala.io.Source.fromFile(filePath.toFile)))
      .flatMap(source => Stream.fromIterator[IO](source.getLines(), 64))
      .map(line => line.split(",").map(_.trim))
      .filter(cols => cols.length == 4)
      .map(cols => Flight(cols(0), cols(1), cols(2), cols(3)))
      .compile
      .toList
  }

  object FlightReloader {
    def reloadFlightsFile(filePath: JPath, reloadInterval: FiniteDuration): Stream[IO, Seq[Flight]] = {
      Stream.eval(Ref.of[IO, Seq[Flight]](Seq.empty)).flatMap { flightsRef =>
        val reloadStream = Stream
          .eval(readFlightsFile(filePath))
          .flatMap(newFlights => Stream.eval(flightsRef.set(newFlights)))

        val periodicReload = Stream.awakeEvery[IO](reloadInterval).evalMap(_ => readFlightsFile(filePath))

        reloadStream.concurrently(periodicReload)
          .evalMap(_ => flightsRef.get)
      }
    }

  }
}


