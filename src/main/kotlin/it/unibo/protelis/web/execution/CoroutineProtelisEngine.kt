package it.unibo.protelis.web.execution

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

abstract class CoroutineProtelisEngine : ProtelisEngine {

  // @Deprecated("Prefer suspend function", replaceWith = ReplaceWith("setupAwait(sourceCode, monitor)"))
  final override fun setup(sourceCode: String, monitor: ProtelisObserver): CompletableFuture<Unit> = GlobalScope
    .launch { setupAwait(sourceCode, monitor) }
    .asCompletableFuture()

  /** @see setup(sourceCode, monitor) */
  abstract suspend fun setupAwait(sourceCode: String, monitor: ProtelisObserver)

  // @Deprecated("Prefer suspend function", replaceWith = ReplaceWith("startAwait()"))
  final override fun start(): CompletableFuture<Unit> = GlobalScope
    .launch { startAwait() }
    .asCompletableFuture()

  /** @see start() */
  abstract suspend fun startAwait()

  // @Deprecated("Prefer suspend function", replaceWith = ReplaceWith("stopAwait()"))
  final override fun stop(): CompletableFuture<Unit> = GlobalScope
    .launch { stopAwait() }
    .asCompletableFuture()

  /** @see stop() */
  abstract suspend fun stopAwait()
}
