package org.indyscala.dataser.data

import java.time.Instant

final case class GpsPoint(timestamp: Instant, latitude: Double, longitude: Double, elevation: Double)
