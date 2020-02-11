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
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SimulatedProtelisEngine() : CoroutineProtelisEngine() {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private var alchemistEngine: AtomicReference<Simulation<Any /* TODO */, Euclidean2DPosition>?> = AtomicReference(null)

  init {
    logger.debug("Simulation container created")
  }

  constructor(sourceCode: String, monitor: ProtelisObserver) : this() {
    GlobalScope.launch { setupAwait(sourceCode, monitor) }.asCompletableFuture().join()
  }

  override suspend fun setupAwait(sourceCode: String, monitor: ProtelisObserver) {
    if (alchemistEngine.get() != null) stopAwait()
    val sim = setupSimulationAsync().await()
    alchemistEngine.set(sim)
    setCode(sourceCode)
    alchemistEngine.get()
      ?.addOutputMonitor(ProtelisUpdateAdapter(monitor))
      ?: throw IllegalStateException("Simulation was not set up correctly")
    alchemistEngine.get()
      ?.awaitFor(Status.INIT)
      ?: throw IllegalStateException("Simulation was not set up correctly")
    GlobalScope.launch { sim.run() }
    logger.debug("Simulation Engine set up correctly")
  }

  override suspend fun startAwait() {
    checkNotNull(alchemistEngine.get())
    alchemistEngine.get()?.play()
    alchemistEngine.get()?.awaitFor(Status.RUNNING) // TODO: should I wait RUNNING instead?
    logger.debug("Simulation started")
  }

  override suspend fun stopAwait() {
    checkNotNull(alchemistEngine.get())
    alchemistEngine.get()?.terminate()
    alchemistEngine.get()?.awaitFor(Status.TERMINATED)
    alchemistEngine.set(null)
    logger.debug("Simulation stopped and deleted")
  }

  /** Asynchronously build a new simulation from a loader of YAML files. */
  private fun setupSimulationAsync(): Deferred<Engine<Any /* TODO */, Euclidean2DPosition>> = GlobalScope.async {
    val loader = YamlLoader(this.javaClass.classLoader.getResourceAsStream("simulation.yml"))
    loader.getDefault<Any /* TODO */, Euclidean2DPosition>()
    Engine<Any /* TODO */, Euclidean2DPosition>(
      loader.getDefault(),
      Long.MAX_VALUE,
      DoubleTime(Double.POSITIVE_INFINITY)
    )
  }

  private fun setCode(sourceCode: String) {
    alchemistEngine
      .get()
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
