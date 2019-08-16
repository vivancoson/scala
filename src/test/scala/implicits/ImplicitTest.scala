package implicits

import org.scalatest.FunSuite

object StringConverters {
  implicit class Ops(val s: String) {
    def toJson = s"{$s}"
    def toXml = s"<$s>"
  }
}

object IntGraphics {
  implicit class Ops(val n: Int) extends AnyVal {
    def stars = "*" * n
    def waves = "~" * n
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

class ImplicitTest extends FunSuite {
  test("implicit parameter") {
    implicit val item = "beers"
    def order(number: Int) (implicit item: String): String = {
      s"$number $item"
    }
    assert(order(2) == "2 beers")
  }

  test("implicit conversion") {
    implicit def intToString(i: Int): String = i.toString
    val three: String = 3
    assert(three == "3")
  }

  test("implicit class") {
    import StringConverters._
    assert("json".toJson == "{json}")
    assert("xml".toXml == "<xml>")
  }

  test("implicit anyval class") {
    import IntGraphics._
    assert(3.stars == "***")
    assert(3.waves == "~~~")
  }

  test("implicit ordering") {
    val unsorted = List(Worker("c", "zspace"), Worker("b", "y"), Worker("a", "x"))
    assert(unsorted.sorted.min == Worker("a", "x"))
  }

  test("implicit folding") {
    import Value._
    val values = List(1, 2, 3).map(n => Value(n))
    val combinedValue = values.foldLeft(Value(0))(_ + _)
    assert(combinedValue.n == 6)
  }

  test("implicitly") {
    case class Name(name: String)
    implicit val implicitName = Name("Fred Flintstone")
    assert(implicitly[Name] == implicitName)
  }
}