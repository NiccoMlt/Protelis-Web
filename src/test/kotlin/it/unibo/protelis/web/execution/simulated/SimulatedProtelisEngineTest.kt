package it.unibo.protelis.web.execution.simulated

import it.unibo.protelis.web.execution.ProtelisObserver
import it.unibo.protelis.web.execution.ProtelisUpdateMessage
import kotlin.test.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Test simulated Protelis engine with Alchemist")
class SimulatedProtelisEngineTest {

  @Test
  @DisplayName("Test that injected code works")
  fun testCodeInject() {
    val simulation = SimulatedProtelisEngine()
    var init = false
    var step = 0
    var done = false
    val monitor = object : ProtelisObserver {
      override fun initialized(update: ProtelisUpdateMessage) {
        init = true
      }

      override fun stepDone(update: ProtelisUpdateMessage) {
        println("Step done")
        step++
      }

      override fun finished(update: ProtelisUpdateMessage) {
        done = true
      }
    }
    simulation.setup(
      """
        def aFunction() = 1
        aFunction() * self.nextRandomDouble()
      """.trimIndent(),
      monitor
    ).join()
    simulation.start().join()
    Thread.sleep(1000)
    simulation.stop().join()
    assertTrue(init, "Simulation should have been initialized")
    assertTrue(step > 0, "Simulation should have run at least one step")
    assertTrue(done, "Simulation should have been terminated")
  }
}
