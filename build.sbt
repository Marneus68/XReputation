name := """XReputation"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.webjars" % "bootstrap" % "3.3.1",
  "org.webjars" % "jquery" % "2.1.3",
  "org.webjars" % "font-awesome" % "4.2.0",
  "org.webjars" % "angularjs" % "1.3.8",
  "com.google.code.gson" % "gson" % "2.2",
  "org.jsoup" % "jsoup" % "1.8.1",
  "org.apache.stanbol" % "org.apache.stanbol.enhancer.engines.htmlextractor" % "0.12.0",
  "net.sf.saxon" % "Saxon-HE" % "9.6.0-4",
  "com.ning" % "async-http-client" % "1.8.14"
)

javacOptions += "-Xlint:deprecation"
