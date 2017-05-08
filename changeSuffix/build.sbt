name := "changeSuffix"

scalaVersion := "2.12.2"

version := "1.0-SNAPSHOT"

val mySourceGenerator = taskKey[Seq[File]]("My Generator")

def generate(src:File, dst: File): Seq[File] = {
    import _root_.java.nio.file.{Files => JavaFiles}
    import _root_.java.nio.file.StandardCopyOption._
    val sourceFiles = Option(src.list) getOrElse Array() filter (_.endsWith(".txt"))
    if (sourceFiles.nonEmpty) dst.mkdirs()
    for (file <- sourceFiles) yield {
        val srcFile = src / file
        val dstFile = dst / ((file take (file lastIndexOf '.')) + ".scala")
        JavaFiles.copy(srcFile.toPath, dstFile.toPath, REPLACE_EXISTING)
        dstFile
    }
}

mySourceGenerator in Compile := generate(
        (baseDirectory / "inputOfSourceGen").value,
        (sourceManaged in compile).value / "scala"
)

// use .taskValue rather than .task
// .task returns sbt.SettingKey[sbt.Task[Seq[sbt.File]]]
// .taskValue returns Seq[sbt.Task[Seq[java.io.File]]]
// .value returns Seq[java.io.File]
// scala.File is alias to java.io.File
sourceGenerators in Compile += (mySourceGenerator in Compile).taskValue

mainClass in run := Some("HelloWorld")