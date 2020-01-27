package it.unibo.protelis.web.execution.simulated

import io.vertx.core.Vertx
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.setupAddress
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class AlchemistVerticleTest {

  /*
   * Note: The Vertx instance is not clustered and has the default configuration.
   * If you need something else then just don’t use injection on that parameter and prepare a Vertx object by yourself.
   */
  @Test
  fun `AlchemistVerticle should deploy correctly`(vertx: Vertx , testContext: VertxTestContext ) {
    vertx.deployVerticle(AlchemistVerticle(), testContext.completing())
  }

  @Test
  fun `AlchemistVerticle should create a simulation for each request`(vertx: Vertx , testContext: VertxTestContext ) {
    val deploy: Checkpoint = testContext.checkpoint()
    val createSimulation: Checkpoint = testContext.checkpoint()

    val verticle = AlchemistVerticle()
    val eb = vertx.eventBus()
    vertx.deployVerticle(verticle, testContext.succeeding {
      deploy.flag()
      eb.request<String>(setupAddress(), TODO()) {
        TODO()
      }
    })
  }
}
