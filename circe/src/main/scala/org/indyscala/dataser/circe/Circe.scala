package org.indyscala.dataser.circe

import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser.parse
import io.circe.syntax._

import org.indyscala.dataser.data.{DataWriter, Sample, Telemetry}

object Circe extends App {
  implicit val SampleEncoder: Encoder[Sample] = deriveEncoder

  val samples: List[Sample] = Telemetry.samples()

  DataWriter.write(
    "circe.ndjson",
    samples.map(_.asJson.noSpaces))

  var i = 0
  DataWriter.read("circe.ndjson").take(5).foreach(line => {
    i = i + 1
    val display = parse(line) match {
      case Right(json) => s"Record $i is:\n$json"
      case Left(failure) => s"parse failed: $failure.message"
    }
    println(display)
  })
}