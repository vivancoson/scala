package implicits

import org.scalatest.{FunSuite, Matchers}

object StringConverters {
  implicit class Ops(val s: String) {
    def toJson = s"{$s}"
    def toXml = s"<$s>"
  }
}

object IntGraphics {
  implicit class Ops(val n: Int) extends AnyVal {
    def stars: String = "*" * n
    def waves: String = "~" * n
  }
}

case class Worker(name: String, task: String)
object Worker {
  implicit def defaultOrdering: Ordering[Worker] = Ordering.by(_.name)
}

case class Value(n: Int)
object Value {
  implicit class ValueCombiner(val v: Value) {
    def +(other: Value): Value = Value(v.n + other.n)
  }
}

class ImplicitTest extends FunSuite with Matchers {
  test("implicit parameter") {
    implicit val item = "beers"
    def order(number: Int) (implicit item: String): String = {
      s"$number $item"
    }
    order(2) shouldEqual "2 beers"
  }

  test("implicit conversion") {
    implicit def intToString(i: Int): String = i.toString
    val three: String = 3
    three shouldEqual "3"
  }

  test("implicit class") {
    import StringConverters._
    "json".toJson shouldEqual "{json}"
    "xml".toXml shouldEqual "<xml>"
  }

  test("implicit anyval class") {
    import IntGraphics._
    3.stars shouldEqual "***"
    3.waves shouldEqual "~~~"
  }

  test("implicit ordering") {
    val unsorted = List(Worker("c", "zspace"), Worker("b", "y"), Worker("a", "x"))
    unsorted.sorted.min shouldEqual Worker("a", "x")
  }

  test("implicit folding") {
    import Value._
    val values = List(1, 2, 3).map(n => Value(n))
    val combinedValue = values.foldLeft(Value(0))(_ + _)
    combinedValue.n shouldEqual 6
  }

  test("implicitly") {
    case class Name(name: String)
    implicit val implicitName = Name("Fred Flintstone")
    implicitly[Name] shouldEqual implicitName
  }
}