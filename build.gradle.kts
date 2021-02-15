import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.30"
  kotlin("kapt") version "1.4.30"
  id("com.github.johnrengelman.shadow") version "6.1.0"
  id("io.vertx.vertx-plugin") version "1.2.0"
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
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.30")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
  implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.3")

  implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("ch.qos.logback:logback-classic:1.2.3")

  val vertxReason = "Also included by Vert.x, enforce common version"

  implementation("com.fasterxml.jackson.core:jackson-core:2.12.1") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.core:jackson-databind:2.12.1") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.12.1") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1") {
    because("Not included by Vert.x, but useful for Kotlin classes serialization")
  }

  implementation("io.vertx:vertx-core:4.0.2")
  implementation("io.vertx:vertx-lang-kotlin:4.0.2")
  implementation("io.vertx:vertx-lang-kotlin-coroutines:4.0.2")
  implementation("io.vertx:vertx-web:4.0.2")

  implementation("it.unibo.alchemist:alchemist-interfaces:9.3.0")
  implementation("it.unibo.alchemist:alchemist-engine:9.3.0")
  implementation("it.unibo.alchemist:alchemist-incarnation-protelis:9.3.0")
  implementation("it.unibo.alchemist:alchemist-time:9.3.0")
  implementation("it.unibo.alchemist:alchemist-loading:9.3.0")

  testImplementation("io.vertx:vertx-junit5:4.0.2")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.7.1") {
    because("Needed to run tests in IDEs that bundle an older version of JUnit")
  }
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
  testRuntimeOnly("org.slf4j:jul-to-slf4j:1.7.30")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

application {
  mainClass.set("io.vertx.core.Launcher")
}

val slf4j = "io.vertx.core.logging.SLF4JLogDelegateFactory"
val vertxLoggerDelegateProp = "vertx.logger-delegate-factory-class-name"
val jvmArgLogger = "-D$vertxLoggerDelegateProp=$slf4j"

vertx {
  mainVerticle = "it.unibo.protelis.web.MainVerticle"
  vertxVersion = "4.0.2"
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
