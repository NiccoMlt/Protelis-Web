package it.unibo.protelis.web.execution.simulated

import it.unibo.alchemist.boundary.interfaces.OutputMonitor
import it.unibo.alchemist.model.interfaces.Environment
import it.unibo.alchemist.model.interfaces.Position
import it.unibo.alchemist.model.interfaces.Reaction
import it.unibo.alchemist.model.interfaces.Time
import it.unibo.protelis.web.execution.ProtelisObserver
import it.unibo.protelis.web.execution.ProtelisUpdateMessage

/**
 * The class is compliant to both [ProtelisObserver] and [OutputMonitor] observer pattern interfaces.
 *
 * To implement it correctly, the [updateMessageGenerator] should be a transformer function that wraps the information
 * extraction from an [Environment].
 *
 * @param T the Concentration Type
 * @param P the [Position] Type
 */
abstract class ProtelisOutputMonitor<T, P : Position<out P>> : ProtelisObserver, OutputMonitor<T, P> {
  abstract override fun initialized(update: ProtelisUpdateMessage)

  abstract override fun stepDone(update: ProtelisUpdateMessage)

  abstract override fun finished(update: ProtelisUpdateMessage)

  /**
   * Transformer function that extracts from an [Alchemist Environment][Environment] the [ProtelisUpdateMessage].
   *
   * @param env the environment to gather information from
   *
   * @return a Protelis update message
   */
  abstract fun updateMessageGenerator(env: Environment<T, P>): ProtelisUpdateMessage

  final override fun finished(env: Environment<T, P>, time: Time, step: Long) =
    finished(updateMessageGenerator(env))

  final override fun initialized(env: Environment<T, P>) =
    initialized(updateMessageGenerator(env))

  final override fun stepDone(env: Environment<T, P>, r: Reaction<T>, time: Time, step: Long) =
    stepDone(updateMessageGenerator(env))
}
