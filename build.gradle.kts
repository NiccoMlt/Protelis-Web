import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("com.github.johnrengelman.shadow")
  id("io.vertx.vertx-plugin")
  id("org.jlleitschuh.gradle.ktlint")
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
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
  implementation("org.jetbrains.kotlin:kotlin-reflect:_")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
  implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:_")

  implementation("io.github.microutils:kotlin-logging-jvm:_")
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
  implementation("it.unibo.alchemist:alchemist-loading:_")
  implementation("it.unibo.alchemist:alchemist-euclidean-geometry:_")

  testImplementation("io.vertx:vertx-junit5:_")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:_") {
    because("Needed to run tests in IDEs that bundle an older version of JUnit")
  }
  testImplementation("org.junit.jupiter:junit-jupiter-api:_")
  testImplementation("org.junit.jupiter:junit-jupiter-params:_")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
  testRuntimeOnly("org.slf4j:jul-to-slf4j:_")
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
    jvmArgs.add(jvmArgLogger)
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
