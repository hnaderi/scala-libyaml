import laika.ast.Image
import sbtcrossproject.CrossProject

ThisBuild / tlBaseVersion := "0.3"

ThisBuild / organization := "dev.hnaderi"
ThisBuild / organizationName := "Hossein Naderi"
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  tlGitHubDev("hnaderi", "Hossein Naderi")
)

ThisBuild / tlSitePublishBranch := Some("main")
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / tlFatalWarnings := false

val Scala212 = "2.12.20"
val Scala213 = "2.13.16"
val Scala3 = "3.3.5"

ThisBuild / scalaVersion := Scala212
ThisBuild / crossScalaVersions := Seq(Scala3, Scala213, Scala212)

lazy val root = tlCrossRootProject.aggregate(
  core,
  libyaml,
  jsyaml,
  snakeyaml,
  backend,
  tests,
  circe,
  `spray-json`,
  `play-json`,
  json4s,
  `zio-json`,
  docs,
  unidocs
)

def module(mname: String): CrossProject => CrossProject =
  _.in(file(s"modules/$mname"))
    .settings(
      name := s"module-$mname",
      moduleName := s"yaml4s-$mname"
    )

lazy val core = module("core") {
  crossProject(NativePlatform, JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .settings(
      description := "yaml4s core"
    )
}

lazy val libyaml = module("libyaml") {
  crossProject(NativePlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core, tests % Test)
    .settings(
      description := "yaml4s Native parser using libyaml"
    )
}

lazy val jsyaml = module("jsyaml") {
  crossProject(JSPlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core, tests % Test)
    .settings(
      description := "yaml4s JS parser using js-yaml",
      Compile / npmDependencies ++= Seq("js-yaml" -> "4.1.0"),
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
    )
    .enablePlugins(ScalaJSBundlerPlugin)
}

lazy val snakeyaml = module("snake") {
  crossProject(JVMPlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core, tests % Test)
    .settings(
      description := "yaml4s JVM parser using snake yaml engine",
      libraryDependencies += "org.snakeyaml" % "snakeyaml-engine" % "2.9"
    )
}

lazy val tests = module("tests") {
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core)
    .enablePlugins(NoPublishPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalameta" %%% "munit" % "1.0.4",
        "org.scalameta" %%% "munit-scalacheck" % "1.0.0"
      )
    )
}

lazy val backend = module("backend") {
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .jsConfigure(_.dependsOn(jsyaml.js))
    .jvmConfigure(_.dependsOn(snakeyaml.jvm))
    .nativeConfigure(_.dependsOn(libyaml.native))
}

lazy val circe = module("circe") {
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core)
    .settings(
      description := "yaml4s integration with circe",
      libraryDependencies += "io.circe" %%% "circe-core" % "0.14.10"
    )
}

lazy val `spray-json` = module("spray-json") {
  crossProject(JVMPlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core)
    .settings(
      description := "yaml4s integration with spray-json",
      libraryDependencies +=
        "io.spray" %%% "spray-json" % "1.3.6"
    )
}

lazy val `play-json` = module("play-json") {
  crossProject(JVMPlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core)
    .settings(
      description := "yaml4s integration with play-json",
      libraryDependencies +=
        ("org.playframework" %%% "play-json" % "3.0.4")
          .cross(CrossVersion.for3Use2_13)
    )
}

lazy val json4s = module("json4s") {
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core)
    .settings(
      description := "yaml4s integration with json4s",
      libraryDependencies += "org.json4s" %%% "json4s-ast" % "4.0.7"
    )
}

lazy val `zio-json` = module("zio-json") {
  crossProject(JVMPlatform, JSPlatform)
    .crossType(CrossType.Pure)
    .dependsOn(core)
    .settings(
      description := "yaml4s integration with zio-json",
      libraryDependencies += "dev.zio" %%% "zio-json" % "0.7.28"
    )
}

lazy val docs = project
  .in(file("site"))
  .dependsOn(
    backend.jvm,
    circe.jvm,
    json4s.jvm,
    `play-json`.jvm,
    `zio-json`.jvm,
    `spray-json`.jvm
  )
  .enablePlugins(Yaml4sSitePlugin)

lazy val unidocs = project
  .in(file("unidocs"))
  .enablePlugins(TypelevelUnidocPlugin)
  .settings(
    name := "yaml4s-docs",
    description := "unified docs for yaml4s",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(
      core.jvm,
      libyaml.native,
      snakeyaml.jvm,
      jsyaml.js,
      backend.jvm,
      circe.jvm,
      `play-json`.jvm,
      json4s.jvm,
      `spray-json`.jvm,
      `zio-json`.jvm
    )
  )

val vcpkgBaseDir = "C:/vcpkg/"
ThisBuild / nativeConfig ~= { c =>
  if (BuildEnv.isWindows) { // vcpkg-installed curl
    c.withCompileOptions(
      c.compileOptions :+ s"-I${vcpkgBaseDir}/installed/x64-windows/include/"
    ).withLinkingOptions(
      c.linkingOptions :+ s"-L${vcpkgBaseDir}/installed/x64-windows/lib/"
    )
  } else c
}
