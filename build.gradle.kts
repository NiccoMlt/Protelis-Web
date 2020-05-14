@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.github.johnrengelman.shadow")
  id("io.vertx.vertx-plugin")
  kotlin("jvm")
  kotlin("kapt")
  id("org.jlleitschuh.gradle.ktlint")
  jacoco
  id("org.jetbrains.dokka")
  idea
}

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation(Kotlin.stdlib.jdk8)
  implementation("org.jetbrains.kotlin:kotlin-reflect:_")
  implementation(KotlinX.coroutines.core)
  implementation(KotlinX.coroutines.jdk8)
  implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:_")
  implementation(KotlinX.collections.immutableJvmOnly)
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-io:_")

  implementation("org.slf4j:slf4j-api:_")
  implementation("ch.qos.logback:logback-classic:_")

  val vertxReason = "Also included by Vert.x, enforce common version"

  implementation("com.fasterxml.jackson.core:jackson-core:_") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.core:jackson-databind:_") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.core:jackson-annotations:_") {
    because(vertxReason)
  }
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:_") {
    because("Not included by Vert.x, but useful for Kotlin classes serialization")
  }

  implementation("io.vertx:vertx-core:_")
  implementation("io.vertx:vertx-lang-kotlin:_")
  implementation("io.vertx:vertx-lang-kotlin-coroutines:_")
  implementation("io.vertx:vertx-web:_")

  implementation("it.unibo.alchemist:alchemist-interfaces:_")
  implementation("it.unibo.alchemist:alchemist-engine:_")
  implementation("it.unibo.alchemist:alchemist-incarnation-protelis:_")
  implementation("it.unibo.alchemist:alchemist-time:_")
  implementation("it.unibo.alchemist:alchemist-loading:_")

  testImplementation("io.vertx:vertx-junit5:_")
  testImplementation("io.vertx:vertx-junit5-web-client:_")
  // testImplementation(Kotlin.test.common)
  // testImplementation(Kotlin.test.junit)
  // testImplementation(Kotlin.test.junit5)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:_") {
    because("Needed to run tests in IDEs that bundle an older version of JUnit")
  }
  testImplementation(Testing.junit.api)
  testImplementation(Testing.junit.params)
  testRuntimeOnly(Testing.junit.engine)
  testRuntimeOnly("org.slf4j:jul-to-slf4j:_")

  dokkaRuntime("org.jetbrains.dokka:dokka-fatjar:_")
}

java {
  sourceCompatibility = Versions.java_version
  targetCompatibility = Versions.java_version
}

application {
  mainClassName = "io.vertx.core.Launcher"
}

val slf4j = "io.vertx.core.logging.SLF4JLogDelegateFactory"
val vertxLoggerDelegateProp = "vertx.logger-delegate-factory-class-name"
val jvmArgLogger = "-D$vertxLoggerDelegateProp=$slf4j"

vertx {
  mainVerticle = "it.unibo.protelis.web.MainVerticle"
  vertxVersion = Versions.io_vertx
  jvmArgs = listOf("jvmArgLogger")
}

tasks {
  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      allWarningsAsErrors = true
      jvmTarget = Versions.jdk_version
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

  "dokka"(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$rootDir/docs"
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
