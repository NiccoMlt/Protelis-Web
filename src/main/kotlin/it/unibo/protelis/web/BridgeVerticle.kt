package it.unibo.protelis.web

import io.netty.handler.codec.http.HttpResponseStatus.MOVED_PERMANENTLY
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpMethod
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.handler.sockjs.sockJSHandlerOptionsOf
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.finishedAddressRegex
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.initializedAddressRegex
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.setupAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.stepDoneAddressRegex
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.stopAddressRegex

/** This verticle bridges EventBus via SockJS. */
class BridgeVerticle(private val port: Int = DEFAULT_PORT) : CoroutineVerticle() {
  private lateinit var eb: EventBus

  companion object {
    private const val DEFAULT_PORT: Int = 8080
    private val logger: Logger = LoggerFactory.getLogger(BridgeVerticle::class.java)
  }

  override fun init(vertx: Vertx, context: Context) {
    super.init(vertx, context)
    eb = vertx.eventBus()
  }

  override suspend fun start() {
    val router: Router = Router.router(vertx)

    router
      .route()
      .handler(LoggerHandler.create(LoggerFormat.TINY))

    router
      .route()
      .handler(
        CorsHandler
          .create(".*.") // fixme: unsafe and probably not needed at all
          .allowCredentials(true)
          .allowedMethods(HttpMethod.values().toSet())
      )

    val sockJSOptions: SockJSHandlerOptions = sockJSHandlerOptionsOf(heartbeatInterval = 2000)
    val sockJSHandler: SockJSHandler = SockJSHandler.create(vertx, sockJSOptions)
    val sockBridgeOptions = BridgeOptions()
      .addInboundPermitted(PermittedOptions().setAddress(setupAddress()))
      .addOutboundPermitted(PermittedOptions().setAddress(setupAddress()))
      .addOutboundPermitted(PermittedOptions().setAddressRegex(initializedAddressRegex.pattern))
      .addOutboundPermitted(PermittedOptions().setAddressRegex(stepDoneAddressRegex.pattern))
      .addOutboundPermitted(PermittedOptions().setAddressRegex(finishedAddressRegex.pattern))
      .addOutboundPermitted(PermittedOptions().setAddressRegex(stopAddressRegex.pattern))
    val sockJsBridge: Router = sockJSHandler.bridge(sockBridgeOptions)
    router.mountSubRouter("/eventbus", sockJsBridge)

    router
      .get("/")
      .handler { routingContext ->
        routingContext
          .response()
          .putHeader("location", "https://protelis-web-frontend.now.sh/")
          .setStatusCode(MOVED_PERMANENTLY.code())
          .end()
      }

    vertx
      .createHttpServer()
      .requestHandler(router)
      .listenAwait(port)

    logger.info("HTTP server started on port $port")
  }
}
