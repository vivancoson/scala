package collection

import org.scalatest.{FunSuite, Matchers}

import scala.collection.mutable

class LinearSeqTest extends FunSuite with Matchers {
  def toList(v: Int) = List(v - 1, v, v + 1)

  test("list") {
    val list = List(1, 2, 3)

    assert(list == 1 :: 2 :: 3 :: Nil)
    assert(list == List(1) ::: List(2, 3))
    assert(list == 1 :: List(2, 3))
    assert(list == 1 +: List(2, 3))
    assert(list == List(1, 2) :+ 3)
    assert(list == List(1) ++ List(2, 3))
    assert(list == List(1) ++: List(2, 3))

    assert(list(2) == 3) // select by index

    assert(list == List(1, 1, 2, 2, 3, 3).distinct)
    assert(list == (List(1) concat List(2, 3)))
    assert(list == (List(-2, -1, 0, 1, 2, 3) intersect List(1, 2, 3, 4, 5, 6)))

    assert(list.length == 3 && list.size == 3)
    assert(list.lengthCompare(list.size) == 0)
    assert(list.lengthCompare(list.size - 1) == 1)
    assert(list.nonEmpty)
    assert(List().isEmpty)

    assert(list.head == 1)
    assert(list.headOption.get == 1)
    assert(list.tail == List(2, 3))
    assert(list.tails.toList == List(List(1, 2, 3), List(2, 3), List(3), List()))
    assert(list.init == List(1, 2))
    assert(list.inits.toList == List(List(1, 2, 3), List(1, 2), List(1), List()))
    assert(list.last == 3)
    assert(list.lastOption.get == 3)
    assert(list.lastIndexOf(3) == 2)
    assert(list.lastIndexOfSlice(List(3)) == 2)
    assert(list.lastIndexWhere(_ > 2) == 2)

    assert(list.collect { case i if i % 2 == 0 => i } == List(2))
    assert(list.collectFirst { case i if i % 2 == 0 => i }.contains(2))
    assert(list.contains(1))
    assert(list.containsSlice(List(2, 3)))
    assert(list.startsWith(List(1, 2)))
    assert(list.endsWith(List(2, 3)))
    assert(list.count(_ > 0) == 3)

    assert((List(1, 2) diff List(2, 3)) == List(1))
    assert((List(2, 3) diff List(1, 2)) == List(3))

    assert((list drop 1) == List(2, 3))
    assert(list.dropWhile(_ < 2) == List(2, 3))
    assert(list.dropRight(1) == List(1, 2))

    assert((list take 2) == List(1, 2))
    assert(list.takeWhile(_ < 3) == List(1, 2))
    assert(list.takeRight(1) == List(3))

    assert(list.min == 1)
    assert(list.minBy(_ * 2) == 1)
    assert(list.max == 3)
    assert(list.maxBy(_ * 2) == 3)

    assert(list.filter(_ > 1) == List(2, 3))
    assert(list.filter(_ > 1).map(_ * 2) == List(4, 6))
    assert(list.filterNot(_ > 1) == List(1))
    assert(list.find(_ > 2).get == 3)

    assert(List(List(1), List(2), List(3)).flatten == list)
    assert(List(Some(1), None, Some(3), None).flatten == List(1, 3))

    assert(list.map(_ * 2) == List(2, 4, 6))
    assert(List("abc").map(_.toUpperCase) == List("ABC"))
    assert(list.map(i => toList(i)) == List(List(0, 1, 2), List(1, 2, 3), List(2, 3, 4)))

    assert(list.flatMap(i => List(i * 2)) == List(2, 4, 6))
    assert(List("abc").flatMap(_.toUpperCase) == List('A', 'B', 'C'))
    assert(list.flatMap(i => toList(i)) == List(0, 1, 2, 1, 2, 3, 2, 3, 4))

    assert(list.foldLeft(List[Int]())( (tail, head) => head :: tail ) == List(3, 2, 1))
    val words = List("Hello, ", "world!")
    assert(words.fold("")(_ + _) == "Hello, world!")
    assert(words.foldLeft("")(_ + _) == "Hello, world!")
    assert(words.foldRight("")(_ + _) == "Hello, world!")

    assert(List(2, 4, 6) === (for (i <- list) yield i * 2))
    assert(List(2, 4, 6) === (for (i <- list if i > 0) yield i * 2))
    assert(list.forall(_ > 0))
    list foreach { i => assert(i > 0) }

    assert(list.groupBy(_ % 2 == 0) == Map(false -> List(1, 3), true -> List(2)))
    assert(list.grouped(1).toList == List(List(1), List(2), List(3)))

    assert(list.indexOf(1) == 0)
    assert(list.indexOfSlice(List(2, 3)) == 1)
    assert(list.indexWhere(_ > 2) == 2)
    assert(list.indices.length == 3)
    for (i <- 0 to 2) assert(list.isDefinedAt(i))

    assert("123" == list.mkString)

    assert(list.padTo(7, 0) == List(1, 2, 3, 0, 0, 0, 0))
    assert(list.patch(0, List(4, 5, 6), 3) == List(4, 5, 6))
    assert((List[Int](2), List[Int](1, 3)) == list.partition(_ % 2 == 0))
    assert(list.permutations.toList == List(List(1, 2, 3), List(1, 3, 2), List(2, 1, 3), List(2, 3, 1), List(3, 1, 2), List(3, 2, 1)))
    assert(list.segmentLength(_ > 0) == 3)
    assert(list.product == 6)

    assert(list == List.range(1, 4))
    assert(list.reduceLeftOption(_ + _).get == 6)
    assert(list.reduceRightOption(_ + _).get == 6)
    assert(list == List(3, 2, 1).reverse)

    assert(list.segmentLength(_ > 0, 0) == 3)

    assert(list == List(3, 2, 1).sortBy(i => i))
    assert(list == List(3, 2, 1).sorted)
    assert(List(1, 2, 3).sortWith(_ > _) == List(3, 2, 1))
    assert(List(3, 2, 1).sortWith(_ < _) == List(1, 2, 3))

    assert(list.scan(0)(_ + _) == List(0, 1, 3, 6))
    assert(list.scanLeft(0)(_ + _) == List(0, 1, 3, 6))
    assert(list.scanRight(0)(_ + _) == List(6, 5, 3, 0))

    assert(list.slice(0, 2) == List(1, 2))
    assert(List(List(1), List(2), List(3)) == list.sliding(1).toList)
    assert((List[Int](1), List[Int](2, 3)) == list.span(_ < 2))
    assert((List[Int](1, 2), List[Int](3)) == list.splitAt(2))
    assert(list.sum == 6)

    assert(List(Set(1, 2), Set(3, 4), Set(5, 6)).transpose == List(List(1, 3, 5), List(2, 4, 6)))
    assert(List(1, 2, 1) == list.updated(index = 2, elem = 1))
    assert(List(2, 4, 6) == list.withFilter(_ > 0).map(_ * 2))

    assert((1 to 100).map(_ % 10).filter(_ > 5).sum == 300) // strict, slowest
    assert((1 to 100).view.map(_ % 10).filter(_ > 5).sum == 300)  // non-strict, fast
    assert((1 to 100).iterator.map(_ % 10).filter(_ > 5).sum == 300)  // non-strict, fastest
    assert((1 to 100).to(LazyList).map(_ % 10).filter(_ > 5).sum == 300)  // non-strict, fastest

    assert((List[Int](1, 3),List[Int](2, 4)) == List((1, 2), (3, 4)).unzip)
    assert(List((1,3), (2,4)) == (List(1, 2) zip List(3, 4)))
    assert(List((1,3), (2,4), (3,5)) == List(1, 2, 3).zipAll(List(3, 4, 5), 0, 1))
    assert(List((1,0), (2,1), (3,2)) == list.zipWithIndex)
  }

  test("lazy list") {
    val numberOfEvens = (1 to 100).to(LazyList).count(_ % 2 == 0)
    assert(numberOfEvens == 50)
  }

  test("list buffer") {
    val buffer = mutable.ListBuffer(1, 2)
    assert((buffer += 3) == mutable.ListBuffer(1, 2, 3))
    assert((buffer -= 3) == mutable.ListBuffer(1, 2))
    assert((buffer -= 2) == mutable.ListBuffer(1))
    assert((buffer -= 1) == mutable.ListBuffer())
  }
}