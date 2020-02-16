package it.unibo.protelis.web

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.core.logging.SLF4JLogDelegateFactory
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle

/**
 * Entrypoint Verticle, launched by [Vert.x Launcher][io.vertx.core.Launcher];
 * it deploys all other verticles to start the application up.
 *
 * It also automatically sets logger delegate for [SLF4J][io.vertx.core.logging.SLF4JLogDelegate].
 */
class MainVerticle : AbstractVerticle() {
  companion object {
    private const val LOGGER_DELEGATE_FACTORY_PROPERTY = "vertx.logger-delegate-factory-class-name"
  }

  init {
    DatabindCodec.mapper().registerKotlinModule()
  }

  override fun start(startPromise: Promise<Void>) {
    if (System.getProperty(LOGGER_DELEGATE_FACTORY_PROPERTY) == null) {
      System.setProperty(
        LOGGER_DELEGATE_FACTORY_PROPERTY,
        SLF4JLogDelegateFactory::class.qualifiedName ?: throw ClassNotFoundException()
      )
    }

    val port: Int? = System.getenv()["PORT"]?.toIntOrNull()

    vertx.deployVerticle(if (port !== null) BridgeVerticle(port) else BridgeVerticle()
    ) { backendResult ->
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
