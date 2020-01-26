package it.unibo.protelis.web.execution

import io.vertx.core.eventbus.EventBus

class EventBusProtelisObserver(private val eb: EventBus) : ProtelisObserver {

  override fun initialized(update: ProtelisUpdateMessage) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun stepDone(update: ProtelisUpdateMessage) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun finished(update: ProtelisUpdateMessage) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
