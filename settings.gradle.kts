import de.fayard.refreshVersions.bootstrapRefreshVersions

buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
    classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
    classpath("org.danilopianini:refreshversions-aliases:+")
  }
}

bootstrapRefreshVersions(listOf(
  org.danilopianini.VersionAliases.additionalAliases,
  """
    |io.vertx:*
    |^^.^^^^^
    |org.slf4j:*
    |^^^.^^^^^
    |org.junit.platform:junit-platform-*
    |                   ^^^^^^^^^^^^^^
    |com.fasterxml.jackson.core:jackson-*
    |    ^^^^^^^^^.^^^^^^^
    |com.fasterxml.jackson.module:jackson-module-*
    |    ^^^^^^^^^.^^^^^^^
    """.trimMargin()
))

plugins {
  id("com.gradle.enterprise").version("3.2")
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

rootProject.name = "protelis-on-web"
