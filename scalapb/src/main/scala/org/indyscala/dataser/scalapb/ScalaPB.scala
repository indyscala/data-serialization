package org.indyscala.dataser.scalapb

import java.time.Instant

import org.indyscala.dataser.data.{DataWriter, Sample, Telemetry}
import org.indyscala.dataser.scalapb.telemetry.{Coordinates, Timestamp, Sample => PBSample}
import scalapb.Encoding

object ScalaPB extends App {
  private implicit def instantToTimestamp(i: Instant): Timestamp = Timestamp(msSinceEpoch = i.toEpochMilli)

  val samples: List[Sample] = Telemetry.samples()

  val example = PBSample()
    .withTimestamp(Instant.now)
    .withOdometer(123456)
    .withCoordinates(Coordinates(latitude = 35.20, longitude = -86.87))

  println(s"length ${example.toByteArray.length}:\n")
  println(example.toProtoString)


  DataWriter.write(
    "protobuf.ndb64pb",
    samples.map(asProtoBase64))

  var i = 0
  DataWriter.read("protobuf.ndb64pb").take(5).foreach(line => {
    i = i + 1
    val display = PBSample.parseFrom(Encoding.fromBase64(line)).toProtoString
    println(display)
  })

  private def asProtoBase64(s: Sample) = {
    val pb = PBSample()
      .withTimestamp(s.timestamp)
      .withOdometer(s.odometer)
      .withCoordinates(Coordinates(latitude = s.latitude, longitude = s.longitude, elevation = s.elevation))
      .withDistance(s.distance.toFloat)

    Encoding.toBase64(
      s.location.map(pb.withLocation).getOrElse(pb)
        .toByteArray)
  }
}
