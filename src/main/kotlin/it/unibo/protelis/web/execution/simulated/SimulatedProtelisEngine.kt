package it.unibo.protelis.web.execution.simulated

import it.unibo.alchemist.core.implementations.Engine
import it.unibo.alchemist.core.interfaces.Simulation
import it.unibo.alchemist.loader.Loader
import it.unibo.alchemist.loader.YamlLoader
import it.unibo.alchemist.model.implementations.positions.Euclidean2DPosition
import it.unibo.alchemist.model.implementations.times.DoubleTime
import it.unibo.protelis.web.execution.ProtelisEngine
import it.unibo.protelis.web.execution.ProtelisObserver

class SimulatedProtelisEngine() : ProtelisEngine {
  private var alchemistEngine: Simulation<Any /* TODO */, Euclidean2DPosition>? = null

  constructor(sourceCode: String, monitor: ProtelisObserver) : this() {
    setup(sourceCode, monitor)
  }

  override fun setup(sourceCode: String, monitor: ProtelisObserver) {
    if (alchemistEngine != null) stop()
    setupSimulation()
    alchemistEngine?.addOutputMonitor(ProtelisUpdateAdapter(monitor))
  }

  override fun start() {
    checkNotNull(alchemistEngine)
    alchemistEngine?.play()
  }

  override fun stop() {
    checkNotNull(alchemistEngine)
    alchemistEngine?.terminate()
    // alchemistEngine?.waitFor() // TODO: enable coroutine await for simulator termination
    alchemistEngine = null
  }

  private fun setupSimulation() {
    val loader: Loader = YamlLoader(this.javaClass.classLoader.getResourceAsStream("simulation.yml"))
    alchemistEngine = Engine<Any /* TODO */, Euclidean2DPosition>(
      loader.getDefault(),
      Long.MAX_VALUE,
      DoubleTime(Double.POSITIVE_INFINITY)
    )
  }
}
