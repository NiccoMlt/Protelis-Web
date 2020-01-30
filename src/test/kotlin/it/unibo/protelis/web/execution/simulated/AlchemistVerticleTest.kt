package it.unibo.protelis.web.execution.simulated

import io.vertx.core.Vertx
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.junit5.Checkpoint
import io.vertx.junit5.Timeout
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import it.unibo.protelis.web.execution.ProtelisUpdateMessage
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.finishedAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.initializedAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.setupAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.stepDoneAddress
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@DisplayName("AlchemistVerticle tests with Vert.x TestContext")
@ExtendWith(VertxExtension::class)
class AlchemistVerticleTest {
  private val logger: Logger = LoggerFactory.getLogger(AlchemistVerticleTest::class.java)

  /*
   * Note: The Vertx instance is not clustered and has the default configuration.
   * If you need something else then just don’t use injection on that parameter and prepare a Vertx object by yourself.
   */
  @Test
  @DisplayName("AlchemistVerticle should deploy correctly")
  fun `AlchemistVerticle should deploy correctly`(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(AlchemistVerticle(), testContext.completing())
  }

  @Test
  @DisplayName("AlchemistVerticle should create a simulation for each request")
  @Timeout(value = Int.MAX_VALUE, timeUnit = TimeUnit.SECONDS) // Yeah, it's very slow to start
  fun `AlchemistVerticle should create a simulation for each request`(vertx: Vertx, testContext: VertxTestContext) {
    val deploy: Checkpoint = testContext.checkpoint()
    val createSimulation: Checkpoint = testContext.checkpoint(3)

    val verticle = AlchemistVerticle()
    val eb = vertx.eventBus()
    vertx.deployVerticle(verticle, testContext.succeeding {
      deploy.flag()

      val sourceCode =
        """
          def aFunction() {
            1
          }
          aFunction() * self.nextRandomDouble()
        """
          .trimIndent()

      eb.request<String>(setupAddress(), sourceCode) {
        if (it.succeeded()) {
          val id = it.result().body()
          logger.info("Received simulation ID: $id")
          createSimulation.flag()
          eb.consumer<ProtelisUpdateMessage>(initializedAddress(id)) { msg ->
            logger.info("Received initialization message ${msg.body()}")
            createSimulation.flag()
          }
          eb.consumer<ProtelisUpdateMessage>(stepDoneAddress(id)) { msg ->
            logger.info("Received step message ${msg.body()}")
          }
          eb.consumer<ProtelisUpdateMessage>(finishedAddress(id)) { msg ->
            logger.info("Received ending message ${msg.body()}")
            createSimulation.flag()
          }
        } else {
          Assertions.fail(it.cause())
        }
      }
    })
  }
}
