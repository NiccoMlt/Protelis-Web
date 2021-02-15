package it.unibo.protelis.web.execution.simulated

import it.unibo.alchemist.boundary.interfaces.OutputMonitor
import it.unibo.alchemist.model.interfaces.Environment
import it.unibo.alchemist.model.interfaces.Position
import it.unibo.protelis.web.execution.ProtelisNode
import it.unibo.protelis.web.execution.ProtelisObserver
import it.unibo.protelis.web.execution.ProtelisUpdateMessage

/**
 * Adapter class used to wrap a [Protelis Observer][ProtelisObserver] in an [Alchemist OutputMonitor][OutputMonitor].
 *
 * @param T the Concentration Type
 * @param P the [Position] Type
 * @param observer the Protelis Observer to adapt
 */
class ProtelisUpdateAdapter<T, P : Position<out P>>(
  private val observer: ProtelisObserver
) : ProtelisOutputMonitor<T, P>() {
  override fun initialized(update: ProtelisUpdateMessage) = observer.initialized(update)

  override fun stepDone(update: ProtelisUpdateMessage) = observer.stepDone(update)

  override fun finished(update: ProtelisUpdateMessage) = observer.finished(update)

  override fun updateMessageGenerator(env: Environment<T, P>): ProtelisUpdateMessage = envToUpdateMsg(env)

  companion object {
    /**
     * Transformer function that extracts from an [Alchemist Environment][Environment] the [ProtelisNode] infos.
     *
     * @param T the Concentration Type
     * @param P the [Position] Type
     * @param env the environment to gather information from
     *
     * @return a Protelis update message
     */
    internal fun <T, P : Position<out P>> envToUpdateMsg(env: Environment<T, P>): ProtelisUpdateMessage =
      ProtelisUpdateMessage(
        nodes = env
          .nodes
          .map { it.id.toString() to env.getPosition(it) }
          .map { it.first to (it.second.coordinates[0] to it.second.coordinates[1]) }
          .map { ProtelisNode(it.first, it.second) }
          .toList(),
        envSize = env
          .size
          .asSequence()
          .zipWithNext()
          .first()
    )
  }
}
