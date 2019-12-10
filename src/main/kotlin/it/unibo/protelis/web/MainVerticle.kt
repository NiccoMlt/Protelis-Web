package it.unibo.protelis.web

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.logging.SLF4JLogDelegateFactory
import it.unibo.protelis.web.backend.BackendVerticle

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

  override fun start(startPromise: Promise<Void>) {
    if (System.getProperty(LOGGER_DELEGATE_FACTORY_PROPERTY) == null) {
      System.setProperty(
        LOGGER_DELEGATE_FACTORY_PROPERTY,
        SLF4JLogDelegateFactory::class.qualifiedName ?: throw ClassNotFoundException()
      )
    }

    vertx.deployVerticle(BackendVerticle()) {
      if (it.succeeded()) {
        startPromise.complete()
      } else {
        startPromise.fail(it.cause())
      }
    }
  }
}
