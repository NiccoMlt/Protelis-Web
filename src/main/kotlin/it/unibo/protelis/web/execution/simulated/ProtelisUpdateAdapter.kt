package it.unibo.protelis.web.execution.simulated

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
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
    private val logger: Logger = LoggerFactory.getLogger(ProtelisUpdateAdapter::class.java)

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
          .map {
            val id = it.id.toString()
            val pos = env.getPosition(it).cartesianCoordinates
            val optVal = it
              .contents
              .filterKeys { molecule -> molecule.name == "default_module:default_program" }
              .map { (molecule, _) -> it.getConcentration(molecule) }
            val value = if (optVal.isNotEmpty()) optVal[0].toString() else ""
            if (value != "") {
              logger.info("Node $id: $value")
            }
            ProtelisNode(id, pos[0] to pos[1], value)
          }
          .toList(),
        envSize = env
          .size
          .asSequence()
          .zipWithNext()
          .first()
      )
  }
}
