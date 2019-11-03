package org.indyscala.dataser.data

import java.time.Instant

/**
 * @param timestamp time sample was taken, likely from GPS
 * @param odometer in miles
 * @param latitude decimal value
 * @param longitude decimal value
 * @param elevation in meters
 * @param location optional description of the location
 * @param distance since previous sample in meters
 */
final case class Sample(timestamp: Instant, odometer: Int, latitude: Double, longitude: Double, elevation: Double, location: Option[String], distance: Double)
