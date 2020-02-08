package it.unibo.protelis.web.execution

import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch

abstract class CoroutineProtelisEngine : ProtelisEngine {

  final override fun setup(sourceCode: String, monitor: ProtelisObserver): CompletableFuture<Unit> = GlobalScope
    .launch { setupAwait(sourceCode, monitor) }
    .asCompletableFuture()

  /** @see setup(sourceCode, monitor) */
  abstract suspend fun setupAwait(sourceCode: String, monitor: ProtelisObserver)

  final override fun start(): CompletableFuture<Unit> = GlobalScope
    .launch { startAwait() }
    .asCompletableFuture()

  /** @see start() */
  abstract suspend fun startAwait()

  final override fun stop(): CompletableFuture<Unit> = GlobalScope
    .launch { stopAwait() }
    .asCompletableFuture()

  /** @see stop() */
  abstract suspend fun stopAwait()
}
