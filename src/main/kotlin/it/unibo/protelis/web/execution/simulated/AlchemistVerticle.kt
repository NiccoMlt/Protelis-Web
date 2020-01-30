package it.unibo.protelis.web.execution.simulated

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.kotlin.coroutines.CoroutineVerticle
import it.unibo.protelis.web.execution.CoroutineProtelisEngine
import it.unibo.protelis.web.execution.EventBusProtelisObserver
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlchemistVerticle : CoroutineVerticle() {
  private val engines: MutableMap<String, CoroutineProtelisEngine> = mutableMapOf()
  private lateinit var eb: EventBus

  override fun init(vertx: Vertx, context: Context) {
    super.init(vertx, context)
    eb = vertx.eventBus()
  }

  override suspend fun start() {
    eb.consumer<String>("$BASE_ADDRESS.$SETUP_SUFFIX") { msg ->
      val id = addressIdGen()
      val source = msg.body()
      val sim = SimulatedProtelisEngine()
      engines += id to sim
      msg.reply(id)
      sim
        .setup(source, EventBusProtelisObserver(eb, id))
        .thenCompose { sim.start() }
    }
  }

  companion object {
    private const val BASE_ADDRESS = "protelis.web.exec"

    private const val INITIALIZED_SUFFIX = "init"
    fun initializedAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$INITIALIZED_SUFFIX"

    private const val STEP_DONE_SUFFIX = "step"
    fun stepDoneAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$STEP_DONE_SUFFIX"

    private const val FINISHED_SUFFIX = "end"
    fun finishedAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$FINISHED_SUFFIX"

    private const val SETUP_SUFFIX = "setup"
    fun setupAddress(): String = "$BASE_ADDRESS.$SETUP_SUFFIX"

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
      "${LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}@$address"
  }
}
