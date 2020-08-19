
val baseName = "Create-Order"
name := baseName
organization in ThisBuild := "com.hbc"
version in ThisBuild := "1.0.0-SNAPSHOT"
scalaVersion := "2.13.2"
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint:unused")
val circeVersion = "0.12.3"
val akkaVersion = "2.6.5"
val akkaStreamAlpakkaFileVersion = "1.1.2"
val logbackVersion = "1.2.3"
lazy val dispatchV = "0.11.3"
lazy val root = project.in(file("."))
  .aggregate(streamlets)
val streamlets = project.in(file("streamlets")).enablePlugins(ScalaxbPlugin).settings(
    name := s"$baseName-test",
    libraryDependencies ++= Seq(
      //dispatch,
      "ch.qos.logback" %  "logback-classic" % logbackVersion,
      "org.scalactic" %% "scalactic" % "3.1.1",
      "org.scalatest" %% "scalatest" % "3.1.1" % "test",
      "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "0.17",
      "com.amazonaws"      %  "aws-java-sdk-s3"        % "1.11.396",
      "com.lightbend.akka" %% "akka-stream-alpakka-jms" % "2.0.1",
      "javax.jms" % "jms" % "1.1",
      "com.ibm.mq" % "com.ibm.mq.allclient" % "9.1.1.0",
      "com.sksamuel.avro4s" %% "avro4s-core" % "3.1.1",
      "com.julianpeeters" %% "avrohugger-core" % "1.0.0-RC19",
      "com.lightbend.akka" %% "akka-stream-alpakka-xml" % "2.0.1",
      "io.spray" %% "spray-json" % "1.3.2",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "2.0.1",
      "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"
    )).settings(
  scalaxbDispatchVersion in (Compile, scalaxb) := dispatchV,
  scalaxbPackageName in (Compile, scalaxb)     := "generated")

 mainClass := Some("test2")