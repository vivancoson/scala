package collection

import org.scalatest.FunSuite

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.language.postfixOps

class FunctionsTest extends FunSuite {
  test("diff") {
    val vector = Vector(1, 2)
    val list = List(2, 3)
    assert((vector diff list) == Vector(1))
    assert((list diff vector) == List(3))
  }

  test("map") {
    val list = List(1, 2)
    val result = list map (_ * 2)
    assert(result == List(2, 4))
  }

  test("filter") {
    val vector = Vector(1, 2, 3)
    assert(vector.filter(_ > 1) == Vector(2, 3))
    assert(vector.filter(_ > 1).map(_ * 2) == Vector(4, 6))
  }

  test("flatten") {
    val list = List(List(1, 2), List(3, 4))
    assert(list.flatten == List(1, 2, 3, 4))

    val vector = Vector(Some(1), None, Some(3), None)
    assert(vector.flatten == Vector(1, 3))
  }

  test("flatmap") {
    val vector = Vector("abc")
    assert(vector.flatMap(_.toUpperCase) == Vector('A', 'B', 'C'))

    val map = Map(1 -> "one", 2 -> "two", 3 -> "three")
    assert((1 to map.size flatMap map.get) == Vector("one", "two", "three"))

    def g(v: Int) = List(v - 1, v, v + 1)
    val list = List(1, 2, 3)
    assert(list.map(i => g(i)) == List(List(0, 1, 2), List(1, 2, 3), List(2, 3, 4)))
    assert(list.flatMap(i => g(i)) == List(0, 1, 2, 1, 2, 3, 2, 3, 4))

    val listOfList: List[List[String]] = List(List("a", "b", "c"))
    val flatMappedListOfList = listOfList flatMap (as => as.map(a => a.toUpperCase))
    assert(listOfList.length == 1)
    assert(flatMappedListOfList.length == 3)
  }

  test("fold") {
    val vector = Vector(1, 2, 3)
    assert(vector.foldLeft(3)(_ + _) == 9)
    assert(vector.foldRight(3)(_ + _) == 9)
  }

  test("groupBy") {
    val vector = Vector(1, 2, 3, 4)
    assert(vector.groupBy(_ % 2 == 0) == Map(false -> Vector(1, 3), true -> Vector(2, 4)))
  }

  test("merge") {
    assert(Vector(1, 2, 3) ++ Vector(4, 5, 6) == Vector(1, 2, 3, 4, 5, 6))
    assert((ArrayBuffer(1, 2, 3) ++= List(4, 5, 6)) == ArrayBuffer(1, 2, 3, 4, 5, 6))
    assert(List(1) ::: List(2) == List(1, 2))
    assert((List(1) union List(2)) == List(1, 2))
    assert((List(1, 2, 3, 4) union List(3, 4, 5, 6) distinct) == List(1, 2, 3, 4, 5, 6))
  }

  test("partition") {
    val tupleOfVectors: (Vector[Int], Vector[Int]) = Vector(1, 2, 3, 4).partition(_ % 2 == 0)
    val expectedTupleOfVectors: (Vector[Int], Vector[Int]) = (Vector(2, 4), Vector(1, 3))
    assert(tupleOfVectors == expectedTupleOfVectors)
  }

  test("reduce") {
    val vector = Vector(1, 2, 3)
    assert(vector.reduceLeft(_ - _) == -4)
    assert(vector.reduceRight(_ - _) == 2)
  }

  test("scan") {
    val vector = Vector(1, 2)
    assert(vector.scanLeft(2)(_ + _) == Vector(2, 3, 5))
    assert(vector.scanRight(2)(_ + _) == Vector(5, 4, 2))
  }

  test("sort") {
    assert(Vector("c", "b", "a").sorted == Vector("a", "b", "c"))
    assert(Vector(3, 2, 1).sortWith(_ < _) == Vector(1, 2, 3))
    assert(Vector(1, 2, 3).sortWith(_ > _) == Vector(3, 2, 1))
  }

  test("span") {
    val tupleOfVectors: (Vector[Int], Vector[Int]) = Vector(1, 2, 3, 4).span(_ < 3)
    val expectedTupleOfVectors: (Vector[Int], Vector[Int]) = (Vector(1, 2), Vector(3, 4))
    assert(tupleOfVectors == expectedTupleOfVectors)
  }

  test("splitAt") {
    val tupleOfVectors: (Vector[Int], Vector[Int]) = Vector(1, 2, 3, 4).splitAt(2)
    val expectedTupleOfVectors: (Vector[Int], Vector[Int]) = (Vector(1, 2), Vector(3, 4))
    assert(tupleOfVectors == expectedTupleOfVectors)
  }

  test("unzip") {
    val tupleOfLists: (List[AnyVal], List[AnyVal]) = List((1, 2), ('a', 'b')).unzip
    val expectedTupleOfLists: (List[AnyVal], List[AnyVal]) = (List(1, 'a'), List(2, 'b'))
    assert(tupleOfLists == expectedTupleOfLists)
  }

  test("zip") {
    val wives = List("wilma", "betty")
    val husbands = List("fred", "barney")
    assert((wives zip husbands) == List(("wilma", "fred"), ("betty", "barney")))
  }

  test("foreach") {
    val vector = Vector(1, 2, 3)
    vector.foreach(i => assert(i > 0))

    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
    map.foreach((t) => assert(t._1.length > 0 && t._2 > 0))
  }

  test("for") {
    for (i <- 1 to 3) {
      assert(i > 0)
    }

    val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
    for (t <- map) {
      assert(t._1.length > 0 && t._2 > 0)
    }

    val vector = Vector(1, 2, 3)
    val result = for (e <- vector if e > 0) yield e * 2
    assert(result == Vector(2, 4, 6))
  }

  test("for > flatmap > map") {
    val xs = List(2, 4)
    val ys = List(3, 5)
    val forList = for (x <- xs; y <- ys) yield x * y
    val mapList = xs flatMap { e => ys map { o => e * o } }
    assert(forList == List(2 * 3, 2 * 5, 4 * 3, 4 * 5))
    assert(mapList == List(2 * 3, 2 * 5, 4 * 3, 4 * 5))
  }

  test("for > flatmap > flatmap > map") {
    val xs = List(2, 4)
    val ys = List(3, 5)
    val zs = List(1, 6)
    val forList = for (x <- xs; y <- ys; z <- zs) yield x * y * z
    val mapList = xs flatMap { x => ys flatMap { y => { zs map { z => x * y * z } } } }
    assert(forList == List(6, 36, 10, 60, 12, 72, 20, 120))
    assert(mapList == List(6, 36, 10, 60, 12, 72, 20, 120))
  }

  test("for > foreach > map") {
    val xs = List(1, 2)
    var forList = ListBuffer[Int]()
    for (x <- xs) {
      forList += (x * 2)
    }
    val mapList = ListBuffer[Int]()
    xs map (_ * 2) foreach (x => mapList += x)
    assert(forList == ListBuffer(2, 4))
    assert(mapList == ListBuffer(2, 4))
  }

  test("for > if guard > filter") {
    val letters = List("A", "B", "C", "D", "F")
    val forLetters: List[Option[String]] = for (l <- letters if l == "A") yield Some(l)
    val filterLetters = letters filter (l => l == "A") map (l => Some(l))
    assert(forLetters.head.getOrElse("Z") == "A")
    assert(filterLetters.head.getOrElse("Z") == "A")
  }
}