package it.unibo.protelis.web.execution.simulated

import it.unibo.alchemist.core.implementations.Engine
import it.unibo.alchemist.core.interfaces.Simulation
import it.unibo.alchemist.core.interfaces.Status
import it.unibo.alchemist.loader.YamlLoader
import it.unibo.alchemist.model.implementations.positions.Euclidean2DPosition
import it.unibo.alchemist.model.implementations.times.DoubleTime
import it.unibo.alchemist.model.interfaces.Position
import it.unibo.protelis.web.execution.CoroutineProtelisEngine
import it.unibo.protelis.web.execution.ProtelisObserver
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class SimulatedProtelisEngine() : CoroutineProtelisEngine() {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private var alchemistEngine: AtomicReference<Simulation<Any /* TODO */, Euclidean2DPosition>?> = AtomicReference(null)

  init {
    logger.debug("Simulation container created")
  }

  constructor(sourceCode: String, monitor: ProtelisObserver) : this() {
    GlobalScope.launch { setupAwait(sourceCode, monitor) }
  }

  override suspend fun setupAwait(sourceCode: String, monitor: ProtelisObserver) {
    if (alchemistEngine.get() != null) stopAwait()
    val sim = setupSimulationAsync().await()
    alchemistEngine.set(sim)
    setCode(sourceCode)
    alchemistEngine.get()?.addOutputMonitor(ProtelisUpdateAdapter(monitor))
    alchemistEngine.get()?.awaitFor(Status.READY) ?: throw IllegalStateException("Simulation was not set up correctly")
    logger.debug("Simulation Engine set up correctly")
  }

  override suspend fun startAwait() {
    checkNotNull(alchemistEngine.get())
    alchemistEngine.get()?.play()
    alchemistEngine.get()?.awaitFor(Status.RUNNING)
    logger.debug("Simulation started")
  }

  override suspend fun stopAwait() {
    checkNotNull(alchemistEngine.get())
    alchemistEngine.get()?.terminate()
    alchemistEngine.get()?.awaitFor(Status.TERMINATED)
    alchemistEngine.set(null)
    logger.debug("Simulation stopped and deleted")
  }

  /**
   * Asynchronously build a new simulation from a loader of YAML files.
   */
  private fun setupSimulationAsync(): Deferred<Engine<Any, Euclidean2DPosition>> = GlobalScope.async {
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

/**
 * Asynchronous wait for the simulation to reach the selected [Status].
 *
 * @param s The [Status] the simulation must reach before returning from this method
 * @param timeout The maximum lapse of time the caller wants to wait before being resumed (0 means "no limit")
 * @param timeunit The [TimeUnit] used to define "timeout"
 *
 * @return the [Deferred] [Status] of the Simulation at the end of the wait
 *
 * @throws InterruptedException if the wait somehow crashes or if time runs out
 */
fun <T, P : Position<out P>> Simulation<T, P>.waitForAsync(
  s: Status,
  timeout: Long = 0,
  timeunit: TimeUnit = TimeUnit.MILLISECONDS
): Deferred<Status> = GlobalScope.async { waitFor(s, timeout, timeunit) }

/**
 * Suspended function that waits for the simulation to reach the selected [Status].
 *
 * @param s The [Status] the simulation must reach before returning from this method
 * @param timeout The maximum lapse of time the caller wants to wait before being resumed (0 means "no limit")
 * @param timeunit The [TimeUnit] used to define "timeout"
 *
 * @return the [Status] of the Simulation at the end of the wait
 *
 * @throws InterruptedException if the wait somehow crashes or if time runs out
 */
suspend fun <T, P : Position<out P>> Simulation<T, P>.awaitFor(
  s: Status,
  timeout: Long = 0,
  timeunit: TimeUnit = TimeUnit.MILLISECONDS
): Status = waitForAsync(s, timeout, timeunit).await()
