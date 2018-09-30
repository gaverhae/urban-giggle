import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.gaverhae",
      scalaVersion := "2.12.6",
      version      := "app"
    )),
    name := "schibsted",
    libraryDependencies += scalaTest % Test
  )

lazy val commonSettings = Seq(
  test in assembly := {}
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("example.Hello"),
  )
