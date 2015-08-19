package future

import org.scalatest.FunSuite
import rest.AsyncRest

import scala.async.Async._
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

class FutureTest extends FunSuite {
  private implicit val ec = ExecutionContext.global

  test("blocking future") {
    val future: Future[String] = Future { "Hello world!" }
    val result: String = Await.result(future, 1 second)
    assert(result == "Hello world!")
  }

  test("non-blocking future") {
    val helloWorldFuture: Future[String] = Future { "Hello world!" }
    helloWorldFuture onComplete {
      case Success(success) => assert(success == "Hello world!")
      case Failure(failure) => throw failure
    }
  }

  test("non-blocking future with promise") {
    case class Message(text: String)
    def send(message: Message): Future[Message] = {
      val promise = Promise[Message] ()
      ec.execute(new Runnable {
        def run() = try {
          promise.success(message)
        } catch {
          case NonFatal(e) => promise.failure(e)
        }
      })
      promise.future
    }
    val future: Future[Message] = send(Message("Hello world!"))
    future onComplete {
      case Success(message) => assert(message.text == "Hello world!")
      case Failure(failure) => throw failure
    }
  }

  test("dependent futures with map") {
    val helloFuture: Future[String] = Future { "Hello" }
    val worldFuture: Future[String] = helloFuture map { s => s + " world!" }
    worldFuture onComplete {
      case Success(success) => assert(success == "Hello world!")
      case Failure(failure) => throw failure
    }
  }

  test("parallel, dependent futures with flat map") {
    val helloFuture: Future[String] = Future { "Hello" }
    val worldFuture: Future[String] = Future { " world!" }
    val helloWorldFuture: Future[String] = helloFuture flatMap {
      hello =>
        worldFuture map {
          world => hello + world
        }
    }
    helloWorldFuture onComplete {
      case Success(success) => assert(success == "Hello world!")
      case Failure(failure) => throw failure
    }
  }

  test("parallel futures with for comprehension") {
    val helloFuture: Future[String] = Future { "Hello" }
    val worldFuture: Future[String] = Future { " world!" }
    val helloWorldFuture: Future[String] = for {
      hello <- helloFuture
      world <- worldFuture
    } yield hello + world
    helloWorldFuture onComplete {
      case Success(success) => assert(success == "Hello world!")
      case Failure(failure) => throw failure
    }
  }

  test("sequential futures with for comprehension") {
    val helloWorldFuture: Future[String] = for {
      hello <-  Future { "Hello" }
      world <- Future { " world!" }
    } yield hello + world
    helloWorldFuture onComplete {
      case Success(success) => assert(success == "Hello world!")
      case Failure(failure) => throw failure
    }
  }

  test("future sequence") {
    val listOfFutures: List[Future[Int]] = List(Future(1), Future(2))
    val futureOfList: Future[List[Int]] = Future.sequence(listOfFutures)
    val result: Future[Int] = futureOfList.map(_.sum)
    result onSuccess {
      case i: Int => assert(i == 3)
    }
  }

  test("future traverse") {
    val futureOfList: Future[List[Int]] = Future.traverse((1 to 2).toList) (i => Future(i * 1))
    val result: Future[Int] = futureOfList.map(_.sum)
    result onSuccess {
      case i: Int => assert(i == 3)
    }
  }

  test("future fold") {
    val listOfFutures: Seq[Future[Int]] = for (i <- 1 to 2) yield Future(i * 1)
    val result: Future[Int] = Future.fold(listOfFutures) (0) (_ + _) // reduce without (0) arg yields identical result
    result onSuccess {
      case i: Int => assert(i == 3)
    }
  }

  test("future andThen") {
    val result: Future[Int] = Future(Integer.parseInt("one")) andThen { case Failure(e) => assert(e.isInstanceOf[NumberFormatException]) }
    result onFailure {
      case e: Exception => assert(e.isInstanceOf[NumberFormatException])
    }
  }

  test("future recover") {
    val result: Future[Int] = Future(Integer.parseInt("one")) recover { case e: Exception => 0 }
    result onSuccess {
      case i: Int => assert(i == 0)
    }
  }

  test("future fallbackTo") {
    val result: Future[Int] = Future(Integer.parseInt("one")) fallbackTo Future(1)
    result onSuccess {
      case i: Int => assert(i == 1)
    }
  }

  test("future zip") {
    val result: Future[Int] = Future(1) zip Future(2) map { case (x, y) => x + y }
    result onSuccess {
      case i: Int => assert(i == 3)
    }
  }

  test("async") {
    val future: Future[Int] = async {
      val futureOne: Future[Int] = async { 1 }
      val futureTwo: Future[Int] = async { 2 }
      await(futureOne) + await(futureTwo)
    }
    future onComplete {
      case Success(result) => assert(result == 3)
      case Failure(failure) => throw failure
    }
  }

  test("async rest") {
    val future = AsyncRest.asyncJoke
    future onComplete {
      case Success(joke) => assert(joke.nonEmpty)
      case Failure(failure) => throw failure
    }
  }
}