package it.unibo.protelis.web.execution.simulated

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import it.unibo.protelis.web.execution.ProtelisObserver
import it.unibo.protelis.web.execution.ProtelisUpdateMessage
import kotlinx.io.errors.IOException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

@DisplayName("Test simulated Protelis engine with Alchemist")
class SimulatedProtelisEngineTest {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  @Test
  @DisplayName("Test that injected code works")
  fun testCodeInject() {
    val simulation = SimulatedProtelisEngine()

    var init = false
    val step = CompletableFuture<ProtelisUpdateMessage>()
    var done = false

    val monitor = object : ProtelisObserver {
      override fun initialized(update: ProtelisUpdateMessage) {
        logger.debug("Init done")
        init = true
      }

      override fun stepDone(update: ProtelisUpdateMessage) {
        logger.debug("Step done")
        step.complete(update)
      }

      override fun finished(update: ProtelisUpdateMessage) {
        logger.debug("End done")
        done = true
      }
    }

    simulation
      .setup(
        this::class.java.classLoader.getResource("code.pt")
          ?.readText()
          ?: throw IOException("Code file not found"),
        monitor
      )
      .thenCompose { simulation.start() }
      .thenCompose { step }
      .thenCompose { simulation.stop() }
      .join()

    Assertions.assertTrue(init, "Simulation should have been initialized")
    Assertions.assertNotNull(step.getNow(null), "Simulation should have run at least one step")
    Assertions.assertTrue(done, "Simulation should have been terminated")
  }
}
