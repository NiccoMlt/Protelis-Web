package it.unibo.protelis.web

import io.netty.handler.codec.http.HttpResponseStatus.MOVED_PERMANENTLY
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.bridge.PermittedOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.LoggerFormat
import io.vertx.ext.web.handler.LoggerHandler
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.net.pemKeyCertOptionsOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.handler.sockjs.sockJSHandlerOptionsOf
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.finishedAddressRegex
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.initializedAddressRegex
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.setupAddress
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.stepDoneAddressRegex
import it.unibo.protelis.web.execution.simulated.AlchemistVerticle.Companion.stopAddressRegex

/** This verticle bridges EventBus via SockJS. */
class BridgeVerticle(private val httpPort: Int = DEFAULT_PORT) : CoroutineVerticle() {
  private lateinit var eb: EventBus

  companion object {
    private const val DEFAULT_PORT: Int = 8080
    private const val DEFAULT_SSL_PORT: Int = 8443
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

    val sockJSOptions: SockJSHandlerOptions = sockJSHandlerOptionsOf(heartbeatInterval = 2000)
    val sockJSHandler: SockJSHandler = SockJSHandler.create(vertx, sockJSOptions)
    val sockBridgeOptions = SockJSBridgeOptions()
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
      .listenAwait(httpPort)

    logger.info("HTTP server started on port $httpPort")

    val httpsPort = DEFAULT_SSL_PORT
    vertx
      .createHttpServer(
        httpServerOptionsOf(
          ssl = true,
          pemKeyCertOptions = pemKeyCertOptionsOf(
            keyPath = "key.pem",
            certPath = "certificate.pem"
          )))
      .requestHandler(router)
      .listenAwait(httpsPort)

    logger.info("HTTPS server started on port $httpsPort")
  }
}
