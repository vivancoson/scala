package exception

import org.scalatest.FunSuite

import scala.io.Source
import scala.util.control.Exception._
import scala.util.control.NonFatal
import scala.util.{Success, Try, Using}

class TryTest extends FunSuite {
  def divide(x: String, y: String): Try[Int] = {
    for {
      x <- Try(x.toInt)
      y <- Try(y.toInt)
    } yield x / y
  }

  def fileToLines(file: String): Try[Seq[String]] = Using(Source.fromFile(file)) { source => source.getLines.toSeq }

  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  test("try catch handler") {
    val handler: PartialFunction[Throwable, Unit] = {
      case NonFatal(error) => assert(error.getMessage.nonEmpty); ()
    }
    try "abc".toInt catch handler
  }

  test("try") {
    assert(divide("9", "3").isSuccess)
    assert(divide("9", "3").toOption.contains(3))
    assert(divide("9", "3").get == 3)
    assert(divide("a", "b").isFailure)
    assert(divide("a", "b").toOption.isEmpty)
    assert(divide("a", "b").getOrElse(-1) == -1)
  }

  test("try option") {
    assert(parseInt("a").isEmpty)
    assert(parseInt("1").isDefined)
  }

  test("try using") {
    assert(fileToLines("build.sbt").isSuccess)
    assert(fileToLines("sbt.sbt").isFailure)
  }

  test("try recover") {
    val i = for {
      i <- Try("one".toInt).recover { case _ => 0 }
    } yield i
    assert(i == Success(0))
  }

  test("all catch") {
    assert(allCatch.opt("1".toInt).nonEmpty)
    assert(allCatch.opt("one".toInt).isEmpty)
  }
}