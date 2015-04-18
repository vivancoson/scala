name := "objektwerks.scala"

version := "1.0"

scalaVersion := "2.11.6"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
  "com.typesafe.slick" % "slick_2.11" % "3.0.0-RC3",
  "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT",
  "org.scalaz" % "scalaz-core_2.11" % "7.1.1",
  "com.typesafe" % "config" % "1.2.1",
  "org.scala-lang.modules" % "scala-async_2.11" % "0.9.2",
  "org.scalafx" % "scalafx_2.11" % "8.0.40-R8",
  "net.databinder.dispatch" % "dispatch-core_2.11" % "0.11.2",
  "org.json4s" % "json4s-jackson_2.11" % "3.2.11",
  "com.h2database" % "h2" % "1.4.187",
  "org.scalacheck" % "scalacheck_2.11" % "1.12.2" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)

scalacOptions ++= Seq(
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:higherKinds",
  "-feature",
  "-deprecation",
  "-Xlint",
  "-Xfatal-warnings" // 2.12 flags (blows up ide compiler. "-Xexperimental", "-Ydelambdafy:method"
)

fork in test := true

javaOptions += "-server -Xss1m -Xmx2g"

logLevel := Level.Info