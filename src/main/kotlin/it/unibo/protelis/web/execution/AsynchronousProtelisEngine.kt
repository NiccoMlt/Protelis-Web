package it.unibo.protelis.web.execution

import kotlinx.coroutines.Deferred

/** A ProtelisEngine extension that supports async methods with deferred results. */
interface AsynchronousProtelisEngine : ProtelisEngine {

  /**
   * Build up a connection with some execution environment,
   * specifying the Protelis source code to execute and some kind of monitor.
   *
   * @param sourceCode the Protelis code to execute
   * @param monitor an observer object that monitors the execution and models the output strategy
   *
   * @return a [Deferred] empty result that determines if the setup was successful or not
   *
   * @see setup
   */
  fun setupAsync(sourceCode: String, monitor: ProtelisObserver): Deferred<Unit>

  /**
   * Start the execution of the code.
   *
   * @return a [Deferred] empty result that determines if the starting was successful or not
   *
   * @throws IllegalStateException if the connection was not set up (use [setup] method)
   *
   * @see start
   */
  fun startAsync(): Deferred<Unit>

  /**
   * Stop and tear down the connection.
   *
   * @return a [Deferred] empty result that determines if the starting was successful or not
   *
   * @throws IllegalStateException if the connection was not set up (use [setup] method)
   *
   * @see stop
   */
  fun stopAsync(): Deferred<Unit>
}
