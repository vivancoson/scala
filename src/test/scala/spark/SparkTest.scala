package spark

import com.typesafe.config.ConfigFactory
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.collection.mutable
import scala.io.Source

case class Person(age: Long, name: String)

class SparkTest extends FunSuite with BeforeAndAfterAll {
  val props = ConfigFactory.load("spark.properties")
  val conf = new SparkConf().setMaster(props.getString("spark.master")).setAppName(props.getString("spark.app.name"))
  val context = new SparkContext(conf)
  val streamingContext = new StreamingContext(context, Seconds(1))
  val sqlContext = new SQLContext(context)

  override protected def afterAll(): Unit = {
    super.afterAll()
    streamingContext.stop(stopSparkContext = true, stopGracefully = true)
  }

  test("transformations to action") {
    val rdd = context.makeRDD(Array(1, 2, 3)).cache()
    assert(rdd.filter(_ % 2 == 0).first == 2)
    assert(rdd.filter(_ % 2 != 0).first == 1)
    assert(rdd.map(_ + 1).sum == 9)
    assert(rdd.map(_ + 1).collect sameElements Array(2, 3, 4))
  }

  test("actions") {
    val rdd = context.makeRDD(Array(1, 2, 3)).cache()
    assert(rdd.count == 3)
    assert(rdd.first == 1)
    assert(rdd.min == 1)
    assert(rdd.max == 3)
    assert(rdd.sum == 6)
    assert(rdd.reduce(_ + _) == 6)
    assert(rdd.take(1) sameElements Array(1))
  }

  test("parallelize") {
    val data = 1 to 1000000
    val rdd = context.parallelize(data)
    val result = rdd.filter(_ % 2 == 0).collect()
    assert(result.length == 500000)
  }

  test("text") {
    val rdd = context.textFile("license.mit").cache()
    val totalLines = rdd.count()
    assert(totalLines == 19)

    val selectedWordCount = rdd.filter(_.contains("Permission")).count()
    assert(selectedWordCount == 1)

    val longestLine = rdd.map(l => l.length).reduce((a, b) => Math.max(a, b))
    assert(longestLine == 77)

    val wordCountRdd = rdd.flatMap(l => l.split("\\P{L}+")).map(_.toLowerCase).map(w => (w, 1)).reduceByKey(_ + _).cache()
    val totalWords = wordCountRdd.count()
    assert(totalWords == 96)

    val maxCount = wordCountRdd.values.max()
    val (word, count) = wordCountRdd.filter(_._2 == maxCount).first()
    assert(word == "the" && count == 14)
  }

  test("streaming") {
    val queue = mutable.Queue[RDD[String]]()
    val ds = streamingContext.queueStream(queue)
    queue += context.makeRDD(Source.fromFile("license.mit").getLines().toSeq)
    val wordCountDs = ds.flatMap(l => l.split("\\P{L}+")).map(_.toLowerCase).map(w => (w, 1)).reduceByKey(_ + _)
    wordCountDs.saveAsTextFiles(System.getProperty("user.home") + "/.scala/spark/ds")
    wordCountDs.foreachRDD(rdd => {
      rdd.saveAsTextFile(System.getProperty("user.home") + "/.scala/spark/rdd")
    })
    streamingContext.start()
  }

  test("dataframes") {
    val df = sqlContext.read.json("src/test/resources/spark.data.frame.json.txt")
    df.printSchema()
    df.show()

    val names = df.select("name").orderBy("name").collect()
    names.foreach(println)
    assert(names.length == 4)
    assert(names.head.mkString == "barney")

    val ages = df.select("age").orderBy("age").collect()
    ages.foreach(println)
    assert(ages.length == 4)
    assert(ages.head.getLong(0) == 21)

    var row = df.filter(df("age") > 23).first()
    assert(row.getLong(0) == 24)
    assert(row.getAs[String](1) == "fred")

    row = df.agg(Map("age" -> "max")).first()
    assert(row.getLong(0) == 24)

    row = df.agg(Map("age" -> "avg")).first()
    assert(row.getDouble(0) == 22.5)
  }

  /*
    A json schema is inferred and then sorted alphabetically by field name. This is incorrect behavior.
    Until a fix is found, case classes must be adjusted accordingly.
   */
  test("case class") {
    val personRdd = sqlContext.read.json("src/test/resources/spark.data.frame.json.txt")
      .map(p => Person(p(0).asInstanceOf[Long], p(1).asInstanceOf[String]))
    val personDf = sqlContext.createDataFrame[Person](personRdd)
    personDf.registerTempTable("persons")
    personDf.printSchema()
    personDf.show()
  }
}