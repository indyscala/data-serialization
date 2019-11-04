package org.indyscala.dataser.xtract

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files

import org.indyscala.dataser.data.{DataWriter, Sample, Telemetry}

import scala.xml.PrettyPrinter

object Xtract extends App {
  val PrettyPrinter = new PrettyPrinter(80, 2)

  val samples: List[Sample] = Telemetry.samples()

  // display example XML
  samples.take(3).map(toXml).foreach(x => println(PrettyPrinter.format(x)))

  // save XML for entire trip
  val telemetryXml = <telemetry>{samples.map(toXml)}</telemetry>
  DataWriter.write("telemetry.xml", Array(telemetryXml.toString()))

  var xmlFile = DataWriter.path().resolve("telemetry.xml")
  val inXml = new String(Files.readAllBytes(xmlFile), UTF_8)
  //println(inXml)


  private def toXml(s: Sample): scala.xml.Elem =
    <sample>
      <timestamp>{s.timestamp}</timestamp>
      <odometer>{s.odometer}</odometer>
      <lat>{s.latitude}</lat>
      <lon>{s.longitude}</lon>
      <ele>{s.elevation}</ele>
      <location>{s.location}</location>
      <distance>{s.distance}</distance>
    </sample>
}
