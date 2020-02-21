package it.unibo.protelis.web

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/** Test the MainVerticle. */
@ExtendWith(VertxExtension::class)
class TestMainVerticle {

  /** Test BridgeVerticle deployment. */
  @Test
  @DisplayName("Test MainVerticle deploy")
  fun `deploy MainVerticle`(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { testContext.completeNow() })
  }
}
