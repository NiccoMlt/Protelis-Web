package it.unibo.protelis.web.execution.simulated

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import it.unibo.protelis.web.execution.EventBusProtelisObserver
import it.unibo.protelis.web.execution.ProtelisEngine
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Vert.x Verticle that wraps a collection of [ProtelisEngine]s simulated with Alchemist. */
class AlchemistVerticle : CoroutineVerticle() {
  private val logger: Logger = LoggerFactory.getLogger(this::class.java)
  private val engines: MutableMap<String, SimulatedProtelisEngine> = mutableMapOf()
  private lateinit var eb: EventBus

  override fun init(vertx: Vertx, context: Context) {
    super.init(vertx, context)
    eb = vertx.eventBus()
  }

  override suspend fun start() {
    eb.consumer<String>(setupAddress()) { msg ->
      val id = addressIdGen()
      val source = msg.body()
      createSimulation(id, source)
      msg.reply(id)
    }
  }

  private fun createSimulation(id: String, source: String) {
    val sim = SimulatedProtelisEngine()
    engines += id to sim

    eb.consumer<Unit>(stopAddress(id)) { engines[id]?.stop() }

    vertx.executeBlocking<Unit>({ promise ->
      sim.setup(source, EventBusProtelisObserver(eb, id))
      promise.complete()
    }, { result ->
      vertx.executeBlocking<Unit>({ promise ->
        if (result.succeeded()) {
          sim.start()
          promise.complete()
        } else {
          promise.fail(result.cause())
        }
      }, {
        if (it.succeeded()) {
          logger.info("Simulation $id started successfully")
        } else {
          logger.error("Simulation $id failed with message ${it.cause()}")
        }
      })
    })
  }

  companion object {
    private const val BASE_ADDRESS = "protelis.web.exec"
    private const val ID_REGEX = """\S+"""

    private const val INITIALIZED_SUFFIX = "init"
    /** Get topic address for initialization messages of given simulation. */
    fun initializedAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$INITIALIZED_SUFFIX"
    @JvmField val initializedAddressRegex: Regex = initializedAddress(ID_REGEX).replace(".", """\.""").toRegex()

    private const val STEP_DONE_SUFFIX = "step"
    /** Get topic address for step messages of given simulation. */
    fun stepDoneAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$STEP_DONE_SUFFIX"
    @JvmField val stepDoneAddressRegex: Regex = stepDoneAddress(ID_REGEX).replace(".", """\.""").toRegex()

    private const val FINISHED_SUFFIX = "end"
    /** Get topic address for termination messages of given simulation. */
    fun finishedAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$FINISHED_SUFFIX"
    @JvmField val finishedAddressRegex: Regex = finishedAddress(ID_REGEX).replace(".", """\.""").toRegex()

    private const val STOP_SUFFIX = "stop"
    /** Get topic address for stop requests of given simulation. */
    fun stopAddress(addressId: String): String = "$BASE_ADDRESS.$addressId.$STOP_SUFFIX"
    @JvmField val stopAddressRegex: Regex = stopAddress(ID_REGEX).replace(".", """\.""").toRegex()

    private const val SETUP_SUFFIX = "setup"
    /** Get topic address for setup a new simulation. */
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
