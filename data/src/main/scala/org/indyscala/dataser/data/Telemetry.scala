package org.indyscala.dataser.data

import java.io.File
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import scala.io.Source

import kantan.csv._, kantan.csv.ops._, kantan.csv.generic._, kantan.csv.java8._
import org.gavaghan.geodesy.{Ellipsoid, GeodeticCalculator, GlobalPosition}

import io.circe.parser._

object Telemetry {
  private val TelemetryHeaders = rfc.withHeader("timestamp", "odometer (miles)", "latitude", "longitude", "location", "distance (m)")
  private val GeoCalc = new GeodeticCalculator
  private var geoCodeCounter = new AtomicInteger
  private def reverseGeocodeUrl(apiKey: String, lat: Double, lon: Double) = s"https://us1.locationiq.com/v1/reverse.php?key=$apiKey&lat=$lat&lon=$lon&format=json"

  /**
   * @return [[Seq]] of telemetry samples from telemetry.csv file
   */
  def samples(): Seq[Sample] = getClass.getResource("/telemetry.csv").asUnsafeCsvReader[Sample](TelemetryHeaders).toSeq

  /**
   * Ingest `TN_to_IN.csv` file with basic GPS data from sample trip and augment
   * with distance and location data to create `telemetry.csv` file.  Reverse geocoding
   * to collect description of location is only done for some samples due to work around
   * rate limiting of trial API from LocationIQ.
   */
  def buildCsvSamplesFromGpsData(): Unit = {
    val apiKey = Option(System.getenv("API_KEY"))
    if (apiKey.isEmpty) {
      println("WARN: API_KEY environment variable not defined.  Reverse geocoding will be skipped.")
    }

    val outFile = File.createTempFile("telemetry", ".csv")
    println(s"writing to $outFile")
    val out = outFile.asCsvWriter[Sample](TelemetryHeaders)
    val in = getClass.getResource("/TN_to_IN.csv")

    var previousMileage = 168493.0 // mileage at start of trip
    var previousPoint: Option[GpsPoint] = None
    in.asUnsafeCsvReader[GpsPoint](rfc.withHeader).foreach(point => {
      val calc = previousPoint.map(segmentCalc(_, point))
      val distance = calc.map(c => c.distance).getOrElse(0.0)
      val odometer = previousMileage + metersToMiles(distance)

      val sample = Sample(
        point.timestamp,
        odometer.toInt,
        point.latitude,
        point.longitude,
        point.elevation,
        describePlace(apiKey, point.latitude, point.longitude),
        distance,
      )
      out.write(sample)
      previousMileage = odometer
      previousPoint = Some(point)
    })
    out.close()
  }

  private def describePlace(apiKey: Option[String], lat: Double, lon: Double) = apiKey match {
    case None => None
    case Some(apiKey) => if (geoCodeCounter.getAndIncrement() % 20 == 0) {
      geocodeApiRequest(reverseGeocodeUrl(apiKey, lat, lon))
    } else {
      None
    }
  }

  private def geocodeApiRequest(url: String): Option[String] = {
    Thread.sleep(1000) // only 60 req/min allowed
    parse(Source.fromURL(url).mkString) match {
      case Right(json) => json.hcursor.downField("display_name").as[String].toOption
      case Left(_) => None
    }
  }

  private def segmentCalc(start: GpsPoint, end: GpsPoint): SegmentCalc = {
    val measurement = GeoCalc.calculateGeodeticMeasurement(
      Ellipsoid.WGS84,
      new GlobalPosition(start.latitude, start.longitude, start.elevation),
      new GlobalPosition(end.latitude, end.longitude, end.elevation))
    measurement.getPointToPointDistance

    SegmentCalc(
      measurement.getPointToPointDistance,
      measurement.getPointToPointDistance / Duration.between(start.timestamp, end.timestamp).getSeconds)
  }

  private def metersToMiles(meters: Double) = meters / 1609.344

  def main(args: Array[String]): Unit = buildCsvSamplesFromGpsData()

  /**
   * @param distance in meters
   * @param speed in meters per second
   */
  final case class SegmentCalc(distance: Double, speed: Double)
}
