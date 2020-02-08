package it.unibo.protelis.web.execution.simulated

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import it.unibo.protelis.web.execution.CoroutineProtelisEngine
import it.unibo.protelis.web.execution.EventBusProtelisObserver
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AlchemistVerticle : CoroutineVerticle() {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private val engines: MutableMap<String, CoroutineProtelisEngine> = mutableMapOf()
  private lateinit var eb: EventBus

  override fun init(vertx: Vertx, context: Context) {
    super.init(vertx, context)
    eb = vertx.eventBus()
  }

  override suspend fun start() {
    eb.consumer<String>(setupAddress()) { msg ->
      val id = addressIdGen()
      val source = msg.body()
      val sim = SimulatedProtelisEngine()
      engines += id to sim
      msg.reply(id)
      eb.consumer<Unit>(stopAddress(id)) { engines[id]?.stop() }
      sim
        .setup(source, EventBusProtelisObserver(eb, id))
        .thenCompose { sim.start() }
        .thenRun { logger.info("Simulation $id started") }
    }
  }

  companion object {
    private const val BASE_ADDRESS = "protelis.web.exec"
    private const val ID_REGEX = """\S+"""

    private const val INITIALIZED_SUFFIX = "init"
    fun initializedAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$INITIALIZED_SUFFIX"
    @JvmField val initializedAddressRegex: Regex = initializedAddress(ID_REGEX).replace(".", """\.""").toRegex()

    private const val STEP_DONE_SUFFIX = "step"
    fun stepDoneAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$STEP_DONE_SUFFIX"
    @JvmField val stepDoneAddressRegex: Regex = stepDoneAddress(ID_REGEX).replace(".", """\.""").toRegex()

    private const val FINISHED_SUFFIX = "end"
    fun finishedAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$FINISHED_SUFFIX"
    @JvmField val finishedAddressRegex: Regex = finishedAddress(ID_REGEX).replace(".", """\.""").toRegex()

    private const val STOP_SUFFIX = "stop"
    fun stopAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$STOP_SUFFIX"
    @JvmField val stopAddressRegex: Regex = stopAddress(ID_REGEX).replace(".", """\.""").toRegex()

    private const val SETUP_SUFFIX = "setup"
    fun setupAddress(): String = "$BASE_ADDRESS.$SETUP_SUFFIX"

    /**
     * Generate a unique address for a specific execution.
     *
     * It generates a string in form "<ISO date-time>@<address>".
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
