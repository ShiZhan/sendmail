name := "sendmail"

version := "1.0"

scalaVersion := Option(System.getProperty("scala.version")).getOrElse("2.11.2")

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-email" % "1.3.3",
  "commons-codec" % "commons-codec" % "1.9",
  "com.typesafe" % "config" % "1.2.1",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-log4j12" % "1.7.5",
  "log4j" % "log4j" % "1.2.17"
)
