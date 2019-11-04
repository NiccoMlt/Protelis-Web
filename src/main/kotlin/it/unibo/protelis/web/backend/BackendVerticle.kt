package it.unibo.protelis.web.backend

import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import mu.KotlinLogging

/**
 * This verticle serves the React.JS application and implement OpenAPI contract for REST APIs.
 */
class BackendVerticle(private val port: Int = DEFAULT_PORT) : CoroutineVerticle() {

  companion object {
    private const val DEFAULT_PORT: Int = 8080
  }

  override suspend fun start() {
    val router: Router = Router.router(vertx)
    router.route().handler(LoggerHandler.create(LoggerFormat.SHORT))
    val apiRouter: Router = OpenAPI3RouterFactory
      .createAwait(vertx, "/openapi.yaml")
      .addHandlerByOperationId("getMessage") {
        it.response().end("Hello React from Vert.x!")
      }
      .router
    router.mountSubRouter("/api/", apiRouter)
    router.get().handler(StaticHandler.create())

    vertx
      .createHttpServer()
      .requestHandler(router)
      .listenAwait(port)

    KotlinLogging.logger {}.info { "HTTP server started on port $port" }
  }
}
