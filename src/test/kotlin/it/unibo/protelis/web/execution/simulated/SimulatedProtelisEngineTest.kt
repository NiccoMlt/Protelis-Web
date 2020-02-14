package it.unibo.protelis.web.execution.simulated

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import it.unibo.protelis.web.execution.ProtelisObserver
import it.unibo.protelis.web.execution.ProtelisUpdateMessage
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Test simulated Protelis engine with Alchemist")
class SimulatedProtelisEngineTest {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  @Test
  @DisplayName("Test that injected code works")
  fun testCodeInject() {
    val simulation = SimulatedProtelisEngine()

    var init = false
    var step = 0
    var done = false

    val monitor = object : ProtelisObserver {
      override fun initialized(update: ProtelisUpdateMessage) {
        logger.debug("Init done")
        init = true
      }

      override fun stepDone(update: ProtelisUpdateMessage) {
        logger.debug("Step done")
        step++
      }

      override fun finished(update: ProtelisUpdateMessage) {
        logger.debug("End done")
        done = true
      }
    }

    simulation
      .setup(
        """
          def aFunction() = 1
          aFunction() * self.nextRandomDouble()
        """.trimIndent(),
        monitor
      )
      .thenCompose { simulation.start() }
      .thenCompose { CompletableFuture<Unit>().completeOnTimeout(Unit, 5, TimeUnit.SECONDS) }
      .thenCompose { simulation.stop() }
      .join()

    Assertions.assertTrue(init, "Simulation should have been initialized")
    Assertions.assertTrue(step > 0, "Simulation should have run at least one step")
    Assertions.assertTrue(done, "Simulation should have been terminated")
  }
}
