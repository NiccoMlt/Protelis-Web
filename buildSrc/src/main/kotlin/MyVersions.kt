import org.gradle.api.JavaVersion

val Versions.java_version: JavaVersion
  get() = JavaVersion.VERSION_11

val Versions.jdk_version: String
  get() = Versions.java_version.toString()

val Versions.node_version: String
  get() = "12.13.1"

val Versions.npm_version: String
  get() = "6.13.2"

val Versions.yarn_version: String
  get() = "1.21.1"
