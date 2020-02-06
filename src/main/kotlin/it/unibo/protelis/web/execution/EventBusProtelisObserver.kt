package it.unibo.protelis.web.execution

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.core.json.JsonObject.mapFrom
import io.vertx.ext.web.impl.Utils
import io.vertx.kotlin.core.json.json
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.finishedAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.initializedAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.stepDoneAddress
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)

  override fun initialized(update: ProtelisUpdateMessage) {
    logger.debug("Handle init update: $update")
    eb.publish(initializedAddress(addressId), mapFrom(update))
  }

  override fun stepDone(update: ProtelisUpdateMessage) {
    logger.debug("Handle step update: $update")
    eb.publish(stepDoneAddress(addressId), mapFrom(update))
  }

  override fun finished(update: ProtelisUpdateMessage) {
    logger.debug("Handle end update: $update")
    eb.publish(finishedAddress(addressId), mapFrom(update))
  }
}
