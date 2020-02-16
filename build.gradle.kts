@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.github.johnrengelman.shadow") version Versions.com_github_johnrengelman_shadow_gradle_plugin
  id("io.vertx.vertx-plugin") version Versions.io_vertx_vertx_plugin_gradle_plugin
  kotlin("jvm") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin
  kotlin("kapt") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin
  id("org.jlleitschuh.gradle.ktlint") version Versions.org_jlleitschuh_gradle_ktlint_gradle_plugin
  id("de.fayard.refreshVersions") version Versions.de_fayard_refreshversions_gradle_plugin
  jacoco
  id("org.jetbrains.dokka") version Versions.org_jetbrains_dokka
  idea
}

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))
  implementation(Libs.kotlinx_coroutines_core)
  implementation(Libs.kotlinx_coroutines_jdk8)
  implementation(Libs.kotlinx_io_jvm)
  implementation(Libs.kotlinx_collections_immutable_jvm)
  implementation(Libs.kotlinx_coroutines_io)

  implementation(Libs.slf4j_api)
  implementation(Libs.logback_classic)

  val vertxReason = "Also included by Vert.x, enforce common version"

  implementation(Libs.jackson_core) {
    because(vertxReason)
  }
  implementation(Libs.jackson_databind) {
    because(vertxReason)
  }
  implementation(Libs.jackson_annotations) {
    because(vertxReason)
  }
  implementation(Libs.jackson_module_kotlin) {
    because("Not included by Vert.x, but useful for Kotlin classes serialization")
  }

  implementation(Libs.vertx_core)
  implementation(Libs.vertx_lang_kotlin)
  implementation(Libs.vertx_lang_kotlin_coroutines)
  implementation(Libs.vertx_web)

  implementation(Libs.alchemist_interfaces)
  implementation(Libs.alchemist_engine)
  implementation(Libs.alchemist_incarnation_protelis)
  implementation(Libs.alchemist_time)
  implementation(Libs.alchemist_loading)

  testImplementation(Libs.vertx_junit5)
  testImplementation(Libs.vertx_junit5_web_client)
  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-junit"))
  testRuntimeOnly(Libs.junit_platform_launcher) {
    because("Needed to run tests in IDEs that bundle an older version of JUnit")
  }
  testImplementation(Libs.junit_jupiter_api)
  testImplementation(Libs.junit_jupiter_params)
  testRuntimeOnly(Libs.junit_jupiter_engine)
  testRuntimeOnly(Libs.jul_to_slf4j)

  dokkaRuntime(Libs.dokka_fatjar)
}

java {
  sourceCompatibility = Versions.java_version
  targetCompatibility = Versions.java_version
}

application {
  mainClassName = "io.vertx.core.Launcher"
}

vertx {
  mainVerticle = "it.unibo.protelis.web.MainVerticle"
  vertxVersion = Versions.io_vertx
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
    dependsOn(listOf(clean, build, shadowJar))
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
