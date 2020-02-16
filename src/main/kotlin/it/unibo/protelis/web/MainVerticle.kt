package it.unibo.protelis.web

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.core.logging.Logger
import io.vertx.core.logging.SLF4JLogDelegateFactory
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle
import io.vertx.core.logging.LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME
import io.vertx.core.logging.LoggerFactory

/**
 * Entrypoint Verticle, launched by [Vert.x Launcher][io.vertx.core.Launcher];
 * it deploys all other verticles to start the application up.
 *
 * It also automatically sets logger delegate for [SLF4J][io.vertx.core.logging.SLF4JLogDelegate].
 */
class MainVerticle : AbstractVerticle() {

  init {
    DatabindCodec.mapper().registerKotlinModule()
    // Force the usage of SLF4J if no preference is found
    if (System.getProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME) == null) {
      System.setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory::class.java.name)
    }
    LoggerFactory.initialise()
    LoggerFactory
      .getLogger(LoggerFactory::class.java)
      .trace("Using logger: ${System.getProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME)}")
  }

  override fun start(startPromise: Promise<Void>) {
    val port: Int? = System.getenv()["PORT"]?.toIntOrNull()

    vertx.deployVerticle(if (port !== null) BridgeVerticle(port) else BridgeVerticle()) { backendResult ->
      if (backendResult.succeeded()) {
        vertx.deployVerticle(AlchemistVerticle()) { alchemistResult ->
          if (alchemistResult.succeeded()) {
            startPromise.complete()
          } else {
            startPromise.fail(alchemistResult.cause())
          }
        }
      } else {
        startPromise.fail(backendResult.cause())
      }
    }
  }
}
