package it.unibo.protelis.web.execution.simulated

import it.unibo.alchemist.core.implementations.Engine
import it.unibo.alchemist.core.interfaces.Simulation
import it.unibo.alchemist.core.interfaces.Status
import it.unibo.alchemist.core.interfaces.awaitFor
import it.unibo.alchemist.loader.YamlLoader
import it.unibo.alchemist.model.implementations.positions.Euclidean2DPosition
import it.unibo.alchemist.model.implementations.times.DoubleTime
import it.unibo.protelis.web.execution.CoroutineProtelisEngine
import it.unibo.protelis.web.execution.ProtelisObserver
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SimulatedProtelisEngine : CoroutineProtelisEngine() {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private var alchemistEngine: Simulation<Any, Euclidean2DPosition>? = null

  override suspend fun setupAwait(sourceCode: String, monitor: ProtelisObserver) {
    if (alchemistEngine != null) stopAwait()
    val sim = setupSimulationAsync().await()
    alchemistEngine = sim
    setCode(sourceCode)
    alchemistEngine
      ?.addOutputMonitor(ProtelisUpdateAdapter(monitor))
      ?: throw IllegalStateException("Simulation was not set up correctly")
    alchemistEngine
      ?.awaitFor(Status.INIT)
      ?: throw IllegalStateException("Simulation was not set up correctly")
    GlobalScope.launch { alchemistEngine?.run() ?: throw IllegalStateException() }
    logger.debug("Simulation Engine set up correctly")
  }

  override suspend fun startAwait() {
    checkNotNull(alchemistEngine)
    alchemistEngine
      ?.play()
      ?: throw IllegalStateException()
    alchemistEngine
      ?.awaitFor(Status.RUNNING)
      ?: throw IllegalStateException()
    logger.debug("Simulation started")
  }

  override suspend fun stopAwait() {
    checkNotNull(alchemistEngine)
    alchemistEngine
      ?.terminate()
      ?: throw IllegalStateException()
    alchemistEngine
      ?.awaitFor(Status.TERMINATED)
      ?: throw IllegalStateException()
    alchemistEngine = null
    logger.debug("Simulation stopped and deleted")
  }

  /** Asynchronously build a new simulation from a loader of YAML files. */
  private fun setupSimulationAsync(): Deferred<Engine<Any, Euclidean2DPosition>> = GlobalScope.async {
    Engine<Any, Euclidean2DPosition>(
      YamlLoader(this.javaClass.classLoader.getResourceAsStream("simulation.yml")).getDefault(),
      Long.MAX_VALUE,
      DoubleTime(Double.POSITIVE_INFINITY)
    )
  }

  /** Build a new simulation from a loader of YAML files. */
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
