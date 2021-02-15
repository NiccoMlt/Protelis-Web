import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.github.johnrengelman.shadow") version "5.2.0"
  id("io.vertx.vertx-plugin") version "1.0.3"
  kotlin("jvm") version "1.3.72"
  kotlin("kapt") version "1.3.72"
  id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
  jacoco
  idea
}

repositories {
  mavenCentral()
  jcenter {
    content {
      includeGroup("org.jetbrains.kotlinx")
    }
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.72")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.7")
  implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")
  implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.2")

  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("ch.qos.logback:logback-classic:1.2.3")

  val vertxReason = "Also included by Vert.x, enforce common version"

  implementation("com.fasterxml.jackson.core:jackson-core:2.11.0") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.11.0") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0") {
    because("Not included by Vert.x, but useful for Kotlin classes serialization")
  }

  implementation("io.vertx:vertx-core:3.9.1")
  implementation("io.vertx:vertx-lang-kotlin:3.9.1")
  implementation("io.vertx:vertx-lang-kotlin-coroutines:3.9.1")
  implementation("io.vertx:vertx-web:3.9.1")

  implementation("it.unibo.alchemist:alchemist-interfaces:9.3.0")
  implementation("it.unibo.alchemist:alchemist-engine:9.3.0")
  implementation("it.unibo.alchemist:alchemist-incarnation-protelis:9.3.0")
  implementation("it.unibo.alchemist:alchemist-time:9.3.0")
  implementation("it.unibo.alchemist:alchemist-loading:9.3.0")

  testImplementation("io.vertx:vertx-junit5:3.9.1")
  testImplementation("io.vertx:vertx-junit5-web-client:3.9.1")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.6.2") {
    because("Needed to run tests in IDEs that bundle an older version of JUnit")
  }
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
  testRuntimeOnly("org.slf4j:jul-to-slf4j:1.7.30")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

// application {
//   mainClassName = "io.vertx.core.Launcher"
// }

val slf4j = "io.vertx.core.logging.SLF4JLogDelegateFactory"
val vertxLoggerDelegateProp = "vertx.logger-delegate-factory-class-name"
val jvmArgLogger = "-D$vertxLoggerDelegateProp=$slf4j"

vertx {
  mainVerticle = "it.unibo.protelis.web.MainVerticle"
  vertxVersion = "3.9.1"
  jvmArgs = listOf("jvmArgLogger")
}

tasks {
  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      allWarningsAsErrors = true
      jvmTarget = java.sourceCompatibility.toString()
      javaParameters = true
    }
  }

  test {
    useJUnitPlatform()
    testLogging {
      events(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
      exceptionFormat = TestExceptionFormat.FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true
      showStandardStreams = true
    }
    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // Required by Intellij IDEA
    jvmArgs!!.add(jvmArgLogger)
  }

  jacocoTestReport {
    reports {
      xml.isEnabled = true
      csv.isEnabled = true
      html.isEnabled = true
    }
  }

  jacocoTestCoverageVerification {
    violationRules {
      rule {
        limit {
          minimum = "0.5".toBigDecimal()
        }
      }
    }
  }

  /** task used by Heroku to build executable Jar */
  register("stage") {
    dependsOn(listOf(shadowJar))
  }

  build {
    mustRunAfter(clean)
  }

  shadowJar {
    isZip64 = true
  }
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

buildScan {
  tag(if (System.getenv("CI").isNullOrEmpty()) "Local" else "CI")
  link("VCS", "https://github.com/NiccoMlt/Protelis-Web/tree/${System.getProperty("vcs.branch")}")
  tag(System.getProperty("os.name"))
  if (!System.getenv("CI").isNullOrEmpty()) {
    publishOnFailure()
  }
}
