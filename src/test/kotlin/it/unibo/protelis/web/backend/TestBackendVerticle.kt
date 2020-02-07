package it.unibo.protelis.web.backend

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestBackendVerticle {

  @Test
  @DisplayName("Test BackendVerticle deploy")
  fun `deploy BackendVerticle`(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(BackendVerticle(), testContext.succeeding<String> { testContext.completeNow() })
  }
}
