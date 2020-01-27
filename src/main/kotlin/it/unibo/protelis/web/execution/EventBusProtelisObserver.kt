package it.unibo.protelis.web.execution

import io.vertx.core.eventbus.EventBus
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.BASE_ADDRESS
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.FINISHED_SUFFIX
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.INITIALIZED_SUFFIX
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.STEP_DONE_SUFFIX

/**
 * This observer implementation adapts Protelis Events to publish on [Vert.x Event Bus][EventBus].
 *
 * @param eb the EventBus to publish on
 * @param addressId the unique execution ID to use in EventBus address
 */
class EventBusProtelisObserver(
  private val eb: EventBus,
  private val addressId: String
) : ProtelisObserver {
  override fun initialized(update: ProtelisUpdateMessage) {
    eb.publish("$BASE_ADDRESS.$addressId.$INITIALIZED_SUFFIX", update)
  }

  override fun stepDone(update: ProtelisUpdateMessage) {
    eb.publish("$BASE_ADDRESS.$addressId.$STEP_DONE_SUFFIX", update)
  }

  override fun finished(update: ProtelisUpdateMessage) {
    eb.publish("$BASE_ADDRESS.$addressId.$FINISHED_SUFFIX", update)
  }
}
