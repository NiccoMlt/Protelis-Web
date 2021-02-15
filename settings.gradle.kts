import de.fayard.refreshVersions.bootstrapRefreshVersions

buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
    classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
  }
}

bootstrapRefreshVersions()

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
