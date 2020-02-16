package it.unibo.protelis.web

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import it.unibo.protelis.web.BridgeVerticle
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestBridgeVerticle {

  @Test
  @DisplayName("Test BridgeVerticle deploy")
  fun `deploy BridgeVerticle`(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(BridgeVerticle(), testContext.succeeding { testContext.completeNow() })
  }
}
