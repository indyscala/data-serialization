package org.indyscala.dataser.data

import java.nio.channels.FileChannel
import java.nio.file.{Files, Path, Paths}

import scala.jdk.CollectionConverters._

object DataWriter {

  def path(): Path = Paths.get(getClass.getResource("/").toURI).getParent.getParent

  def write(filename: String, lines: IterableOnce[String]): Unit = {
    val file = path.resolve(filename)
    val writer = Files.newBufferedWriter(file)
    try lines.iterator.foreach(l => {
      writer.write(l)
      writer.write("\n")
    }) finally writer.close()
    val bytes = FileChannel.open(file).size()
    println(s"$bytes bytes written to to $file")
  }

  def read(filename: String): Iterable[String] = {
    val file = path.resolve(filename)
    Files.readAllLines(file).asScala
  }
}
