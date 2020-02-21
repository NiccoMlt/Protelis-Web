package it.unibo.protelis.web.execution.simulated

import it.unibo.protelis.web.execution.ProtelisObserver
import it.unibo.protelis.web.execution.ProtelisUpdateMessage
import java.util.concurrent.CompletableFuture
import kotlinx.io.errors.IOException
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@DisplayName("Test simulated Protelis engine with Alchemist")
class NewSimulatedProtelisEngineTest {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  @Test
  @DisplayName("Test that injected code works")
  fun testCodeInject() {
    val simulation = NewSimulatedProtelisEngine()

    val init = CompletableFuture<ProtelisUpdateMessage>()
    val step = CompletableFuture<ProtelisUpdateMessage>()
    val done = CompletableFuture<ProtelisUpdateMessage>()

    val monitor = object : ProtelisObserver {
      override fun initialized(update: ProtelisUpdateMessage) {
        logger.debug("Init done")
        init.complete(update)
      }

      override fun stepDone(update: ProtelisUpdateMessage) {
        logger.debug("Step done")
        step.complete(update)
      }

      override fun finished(update: ProtelisUpdateMessage) {
        logger.debug("End done")
        done.complete(update)
      }
    }

    simulation.setup(
      this::class.java.classLoader.getResource("code.pt")
        ?.readText()
        ?: throw IOException("Code file not found"),
      monitor
    )
    simulation.start()
    val stepMsg: ProtelisUpdateMessage? = step.get()
    simulation.stop()

    val initMsg: ProtelisUpdateMessage? = init.getNow(null)
    logger.debug("Initialization message: $initMsg")
    logger.debug("First step done message: $stepMsg")
    val doneMsg: ProtelisUpdateMessage? = done.getNow(null)
    logger.debug("Finalization message: $doneMsg")

    assertNotNull(initMsg, "Simulation should have been initialized")
    assertNotNull(stepMsg, "Simulation should have run at least one step")
    assertNotNull(doneMsg, "Simulation should have been terminated")
  }
}
