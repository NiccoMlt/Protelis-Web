package it.unibo.protelis.web.execution.simulated

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
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
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.stopAddress
import java.util.concurrent.TimeUnit
import kotlinx.io.errors.IOException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/** AlchemistVerticle tests with Vert.x TestContext. */
@DisplayName("AlchemistVerticle tests with Vert.x TestContext")
@ExtendWith(VertxExtension::class)
class AlchemistVerticleTest {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  init {
    DatabindCodec.mapper().registerKotlinModule()
  }

  /** AlchemistVerticle should deploy correctly. */
  @Test
  @DisplayName("AlchemistVerticle should deploy correctly")
  fun `AlchemistVerticle should deploy correctly`(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(AlchemistVerticle(), testContext.completing())
  }

  /** AlchemistVerticle should create a simulation for each request. */
  @Test
  @DisplayName("AlchemistVerticle should create a simulation for each request")
  @Timeout(value = 5, timeUnit = TimeUnit.MINUTES) // Yeah, it may be slow to start
  fun `AlchemistVerticle should create a simulation for each request`(vertx: Vertx, testContext: VertxTestContext) {
    val deploy: Checkpoint = testContext.checkpoint()
    val createSimulation: Checkpoint = testContext.checkpoint(2)
    val steps: Checkpoint = testContext.laxCheckpoint()
    val terminateSimulation: Checkpoint = testContext.checkpoint()

    val verticle = AlchemistVerticle()
    val eb = vertx.eventBus()
    vertx.deployVerticle(verticle, testContext.succeeding {
      deploy.flag()

      val sourceCode = this::class.java.classLoader.getResource("code.pt")
        ?.readText()
        ?: throw IOException("Code file not found")

      var oneTime = true

      eb.request<String>(setupAddress(), sourceCode) {
        if (it.succeeded()) {
          val id = it.result().body()
          logger.info("Received simulation ID: $id")
          createSimulation.flag()
          eb.consumer<JsonObject>(initializedAddress(id)) { msg ->
            logger.trace("Received initialization message ${msg.body().mapTo(ProtelisUpdateMessage::class.java)}")
            createSimulation.flag()
          }
          eb.consumer<JsonObject>(stepDoneAddress(id)) { msg ->
            logger.trace("Received step message ${msg.body().mapTo(ProtelisUpdateMessage::class.java)}")
            steps.flag()
            if (oneTime) {
              oneTime = false
              eb.send(stopAddress(id), JsonObject())
            }
          }
          eb.consumer<JsonObject>(finishedAddress(id)) { msg ->
            logger.trace("Received ending message ${msg.body().mapTo(ProtelisUpdateMessage::class.java)}")
            terminateSimulation.flag()
            testContext.verify { testContext.completed() }
          }
        } else {
          Assertions.fail(it.cause())
        }
      }
    })
  }
}
