package it.unibo.protelis.web.execution.simulated

import io.vertx.kotlin.coroutines.CoroutineVerticle
import it.unibo.protelis.web.execution.ProtelisEngine

class AlchemistVerticle : CoroutineVerticle() {
  private var engine: ProtelisEngine? = null

  override suspend fun start() {
  }
}
