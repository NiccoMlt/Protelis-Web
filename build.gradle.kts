@file:Suppress("UnstableApiUsage")

import com.moowork.gradle.node.yarn.YarnTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin
  kotlin("kapt") version Versions.org_jetbrains_kotlin_jvm_gradle_plugin
  application
  id("org.jlleitschuh.gradle.ktlint") version Versions.org_jlleitschuh_gradle_ktlint_gradle_plugin
  id("com.github.node-gradle.node") version Versions.com_github_node_gradle_node_gradle_plugin
  id("de.fayard.refreshVersions") version Versions.de_fayard_refreshversions_gradle_plugin
  jacoco
  id("com.github.johnrengelman.shadow") version Versions.com_github_johnrengelman_shadow_gradle_plugin
  id("io.vertx.vertx-plugin") version Versions.io_vertx_vertx_plugin_gradle_plugin
  id("org.jetbrains.dokka") version Versions.org_jetbrains_dokka
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
  implementation(Libs.kotlinx_io)
  implementation(Libs.kotlinx_io_jvm)
  implementation(Libs.kotlinx_coroutines_io)

  implementation(Libs.org_slf4j_slf4j_api)
  implementation(Libs.ch_qos_logback_logback_classic)

  implementation(Libs.jackson_core) // also included by vertx, enforce common version
  implementation(Libs.jackson_databind) // also included by vertx, enforce common version
  implementation(Libs.jackson_annotations) // also included by vertx, enforce common version
  implementation(Libs.jackson_dataformat_yaml) // yaml helper
  implementation(Libs.jackson_module_kotlin) // kotlin helper

  implementation(Libs.vertx_core)
  implementation(Libs.vertx_lang_kotlin)
  implementation(Libs.vertx_lang_kotlin_coroutines)
  implementation(Libs.vertx_web)
  implementation(Libs.vertx_web_api_contract)

  implementation(Libs.alchemist_interfaces)
  implementation(Libs.alchemist_engine)
  implementation(Libs.alchemist_incarnation_protelis)
  implementation(Libs.alchemist_time)
  implementation(Libs.alchemist_loading)

  testImplementation(Libs.vertx_junit5)
  testImplementation(Libs.vertx_junit5_web_client)
  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-junit"))
  testRuntimeOnly(Libs.junit_platform_launcher)
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

val frontend: String = "src/main/frontend/"
val frontendOut: String = "$frontend/build"

node {
  version = Versions.node_version
  npmVersion = Versions.npm_version
  yarnVersion = Versions.yarn_version
  download = true
  nodeModulesDir = File(frontend)
}

vertx {
  mainVerticle = "it.unibo.protelis.web.MainVerticle"
  vertxVersion = Versions.io_vertx
}

tasks {
  withType<KotlinCompile>().all {
    kotlinOptions {
      allWarningsAsErrors = true
      jvmTarget = Versions.jdk_version
    }
  }

  val buildFrontend by creating(YarnTask::class) {
    args = listOf("build")
    inputs.files(
      "$frontend/package.json", // React package configuration
      "$frontend/yarn.lock", // dependencies lockfile
      "$frontend/tsconfig.json" // TypeScript config
    )
    inputs.dir("$frontend/src") // React sources
    inputs.dir(
      fileTree("$frontend/node_modules") // Node modules ...
        .exclude(".cache") // ... ignoring cache
    )
    outputs.dir(frontendOut)
    dependsOn("yarn")
  }

  val copyToWebRoot by creating(Copy::class) {
    from(frontendOut)
    destinationDir = File("$buildDir/classes/kotlin/main/webroot")
    dependsOn(buildFrontend)
  }

  "processResources"(ProcessResources::class) {
    dependsOn(copyToWebRoot)
  }

  create<YarnTask>("jest") {
    setEnvironment(mapOf("CI" to "true"))
    args = listOf("test")
    dependsOn("yarn")
  }

  test {
    // maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    // maxParallelForks = 1
    useJUnitPlatform {
      includeEngines("junit-jupiter")
    }
    testLogging {
      events.addAll(listOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED))
      exceptionFormat = TestExceptionFormat.FULL
    }
  }

  jacocoTestReport {
    reports {
      xml.isEnabled = true
      csv.isEnabled = true
      html.isEnabled = true
    }
  }

  check {
    dependsOn(jacocoTestReport)
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

buildScan {
  tag(if (System.getenv("CI").isNullOrEmpty()) "Local" else "CI")
  link("VCS", "https://github.com/NiccoMlt/Protelis-Web/tree/${System.getProperty("vcs.branch")}")
  tag(System.getProperty("os.name"))
  if (!System.getenv("CI").isNullOrEmpty()) {
    publishOnFailure()
  }
}
