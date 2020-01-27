package it.unibo.protelis.web.execution.simulated

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.kotlin.coroutines.CoroutineVerticle
import it.unibo.protelis.web.execution.EventBusProtelisObserver
import it.unibo.protelis.web.execution.ProtelisEngine
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlchemistVerticle : CoroutineVerticle() {
  private val engines: MutableMap<String, ProtelisEngine> = mutableMapOf()
  private lateinit var eb: EventBus

  override fun init(vertx: Vertx, context: Context) {
    super.init(vertx, context)
    eb = vertx.eventBus()
  }

  override suspend fun start() {
    eb.consumer<String>("$BASE_ADDRESS.$SETUP_SUFFIX") {
      val id = addressIdGen()
      val source = it.body() // TODO
      engines += id to SimulatedProtelisEngine(source, EventBusProtelisObserver(eb, id))
      // TODO: should split initialization from source receive ?
      // TODO: do something else ?
      engines[id]?.start()
    }
  }

  companion object {
    const val BASE_ADDRESS = "protelis.web.exec"
    const val INITIALIZED_SUFFIX = "init"
    const val STEP_DONE_SUFFIX = "step"
    const val FINISHED_SUFFIX = "end"
    const val SETUP_SUFFIX = "setup"

    /**
     * Generate a unique address for a specific execution.
     *
     * It generates a string in form "<ISO instant>@<address>".
     *
     * Address can be anything (IP, name, etc.) and defaults to "alchemist".
     *
     * @param address the address to use after '@' character
     *
     * @return the (hopefully) unique address for an execution
     */
    fun addressIdGen(address: String = "alchemist"): String =
      "${LocalDateTime.now().format(DateTimeFormatter.ISO_INSTANT)}@$address"
  }
}
