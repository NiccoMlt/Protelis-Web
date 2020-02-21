package it.unibo.protelis.web.execution.simulated

import it.unibo.alchemist.core.implementations.Engine
import it.unibo.alchemist.core.interfaces.Simulation
import it.unibo.alchemist.core.interfaces.Status
import it.unibo.alchemist.core.interfaces.waitForAndCheck
import it.unibo.alchemist.loader.YamlLoader
import it.unibo.alchemist.model.implementations.positions.Euclidean2DPosition
import it.unibo.alchemist.model.implementations.times.DoubleTime
import it.unibo.protelis.web.execution.ProtelisEngine
import it.unibo.protelis.web.execution.ProtelisObserver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** ProtelisEngine implementation that simulates a network of devices. */
class SimulatedProtelisEngine : ProtelisEngine {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private var alchemistEngine: Simulation<Any, Euclidean2DPosition>? = null

  override fun setup(sourceCode: String, monitor: ProtelisObserver) {
    if (alchemistEngine != null) {
      stop()
    }

    alchemistEngine = setupSimulation()
    setCode(sourceCode)
    alchemistEngine
      ?.addOutputMonitor(ProtelisUpdateAdapter(monitor))
      ?: throw IllegalStateException("Simulation was not set up correctly")
    alchemistEngine
      ?.waitForAndCheck(Status.INIT)
      ?: throw IllegalStateException("Simulation was not set up correctly")
    GlobalScope.launch() { alchemistEngine?.run() ?: throw IllegalStateException() }
    logger.debug("Simulation Engine set up correctly")
  }

  override fun start() {
    checkNotNull(alchemistEngine)
    alchemistEngine
      ?.play()
      ?: throw IllegalStateException()
    alchemistEngine
      ?.waitForAndCheck(Status.RUNNING)
      ?: throw IllegalStateException()
    logger.debug("Simulation started")
  }

  override fun stop() {
    checkNotNull(alchemistEngine)
    alchemistEngine
      ?.terminate()
      ?: throw IllegalStateException()
    alchemistEngine
      ?.waitForAndCheck(Status.TERMINATED)
      ?: throw IllegalStateException()
    alchemistEngine = null
    logger.debug("Simulation stopped and deleted")
  }

  private fun setupSimulation(): Engine<Any, Euclidean2DPosition> =
    Engine<Any, Euclidean2DPosition>(
      YamlLoader(this.javaClass.classLoader.getResourceAsStream("simulation.yml")).getDefault(),
      Long.MAX_VALUE,
      DoubleTime(Double.POSITIVE_INFINITY)
    )

  private fun setCode(sourceCode: String) {
    alchemistEngine
      ?.environment
      ?.nodes
      ?.forEach { node ->
        node
          .contents
          .filterKeys { it.name == "_to_exec" }
          .forEach { (mol, _) ->
            node.setConcentration(mol, sourceCode)
            logger.trace("Injecting Protelis code to molecule \"$mol\" of node \"${node.id}\"")
          }
      }
      ?: throw IllegalStateException("Can't set code to non-existent simulation")
  }
}
