package spark

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FunSuite

class SparkTest extends FunSuite {
  test("spark") {
    val conf = new SparkConf().setMaster("local").setAppName("sparky")
    val context = new SparkContext(conf)
    val rdd = context.makeRDD(Array(1, 2, 3))
    assert(rdd.count == 3)
    assert(rdd.min == 1)
    assert(rdd.max == 3)
    assert(rdd.filter(_ % 2 == 0).first == 2)
    assert(rdd.filter(_ % 2 != 0).first == 1)
    assert(rdd.reduce(_ + _) == 6)
  }
}