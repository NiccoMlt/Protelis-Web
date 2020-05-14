import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard:dependencies:0.5.7")
}

bootstrapRefreshVersionsAndDependencies(
  listOf(
    """
    |io.vertx:*
    |   ^^^^^
    """.trimMargin(),
    """
    |org.slf4j:*
    |    ^^^^^
    """.trimMargin(),
    """
    |org.junit.jupiter:junit-jupiter-*
    |    ^^^^^.^^^^^^^
    """.trimMargin(),
    """
    |org.junit.platform:junit-platform-*
    |    ^^^^^.^^^^^^^^
    """.trimMargin(),
    """
    |it.unibo.alchemist:alchemist-*
    |         ^^^^^^^^^
    """.trimMargin(),
    """
    |com.fasterxml.jackson.core:jackson-*
    |              ^^^^^^^
    """.trimMargin(),
    """
    |com.fasterxml.jackson.module:jackson-module-*
    |              ^^^^^^^
    """.trimMargin()
  )
)

plugins {
  id("com.gradle.enterprise").version("3.1.1")
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

rootProject.name = "protelis-on-web"
