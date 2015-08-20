package fx

import javafx.{concurrent => jfxc}

import rest.AsyncRest

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.concurrent.Task
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.VBox

object JokeTask extends Task(new jfxc.Task[String] {
  override def call(): String = {
    Await.result(AsyncRest.asyncJoke, 30 seconds)
  }
})

object RestApp extends JFXApp {
  val jokeLabel = new Label {
    text = "Joke:"
  }

  val jokeText = new TextArea {
    wrapText = true
    text <== JokeTask.value
  }

  val indicator = new ProgressIndicator {
    prefWidth = 50
    progress = -1.0
    visible <== JokeTask.running
  }

  val jokeButton = new Button {
    text = "New Joke"
    disable <== JokeTask.running
    onAction = (e: ActionEvent) => { ExecutionContext.global.execute(JokeTask) }
  }

  val jokePane = new VBox {
    maxWidth = 400
    maxHeight = 400
    spacing = 6
    padding = Insets(6)
    children = List(jokeLabel, jokeText)
  }

  val toolbar = new ToolBar {
    content = List(jokeButton, new Separator(), indicator)
  }

  val appPane = new VBox {
    maxWidth = 400
    maxHeight = 400
    spacing = 6
    padding = Insets(6)
    children = List(toolbar, jokePane)
  }

  stage = new JFXApp.PrimaryStage {
    title.value = "Chuck Norris Jokes"
    scene = new Scene {
      root = appPane
    }
  }
}