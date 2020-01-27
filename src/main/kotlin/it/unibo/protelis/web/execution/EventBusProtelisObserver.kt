package it.unibo.protelis.web.execution

import io.vertx.core.eventbus.EventBus
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.finishedAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.initializedAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.stepDoneAddress

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
    eb.publish(initializedAddress(addressId), update)
  }

  override fun stepDone(update: ProtelisUpdateMessage) {
    eb.publish(stepDoneAddress(addressId), update)
  }

  override fun finished(update: ProtelisUpdateMessage) {
    eb.publish(finishedAddress(addressId), update)
  }
}
