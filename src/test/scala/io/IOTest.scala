package io

import org.scalatest.FunSuite

import scala.collection.MapView
import scala.io.{Codec, Source}
import scala.util.{Try, Using}

class IOTest extends FunSuite {
  val regex = "\\P{L}+"
  val quote = "You can avoid reality, but you cannot avoid the consequences of avoiding reality."

  test("from url") {
    val jokes = Source.fromURL("http://api.icndb.com/jokes/random/").mkString.split(regex)
    assert(jokes.nonEmpty)
  }

  test("from file") {
    val words = Source.fromFile("./LICENSE").mkString.split(regex)
    assert(words.length == 168)
  }

  test("from input stream") {
    val words = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split(regex)
    assert(words.length == 168)
    assert(toWordCountMap(words).size == 95)
  }

  test("from string") {
    val words = Source.fromString(quote).mkString.split(regex)
    assert(words.length == 13)
  }

  test("from chars") {
    val words = Source.fromChars(quote.toCharArray).mkString.split(regex)
    assert(words.length == 13)
  }

  test("from bytes") {
    val words = Source.fromBytes(quote.getBytes(Codec.UTF8.name)).mkString.split(regex)
    assert(words.length == 13)
  }

  test("grouped") {
    val list = Source.fromInputStream(getClass.getResourceAsStream("/license.mit")).mkString.split(regex).toList
    assert(list.length == 168)
    val words = list.grouped(list.length / 8).toList
    assert(words.length == 8)
  }

  test("file to lines") {
    assert(fileToLines("build.sbt").isSuccess)
    assert(fileToLines("sbt.sbt").isFailure)
  }

  def toWordCountMap(words: Array[String]): MapView[String, Int] = {
    words.groupBy((word: String) => word.toLowerCase).view.mapValues(_.length)
  }

  def fileToLines(file: String): Try[Seq[String]] = Using(Source.fromFile(file)) { source => source.getLines.toSeq }
}