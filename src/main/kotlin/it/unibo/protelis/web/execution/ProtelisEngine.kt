package it.unibo.protelis.web.execution

import java.util.concurrent.Future

/** A ProtelisEngine object is some kind of connection with a simulator or actual network of devices. */
interface ProtelisEngine {

  /**
   * Build up a connection with some execution environment,
   * specifying the Protelis source code to execute and some kind of monitor.
   *
   * @param sourceCode the Protelis code to execute
   * @param monitor an observer object that monitors the execution and models the output strategy
   *
   * @return a [Future] that determines if the setup was successful or not
   */
  fun setup(sourceCode: String, monitor: ProtelisObserver): Future<Unit>

  /**
   * Start the execution of the code.
   *
   * @return a [Future] that determines if the starting was successful or not
   *
   * @throws IllegalStateException if the connection was not set up (use [setup] method)
   */
  fun start(): Future<Unit>

  /**
   * Stop and tear down the connection.
   *
   * @return a [Future] that determines if the starting was successful or not
   *
   * @throws IllegalStateException if the connection was not set up (use [setup] method)
   */
  fun stop(): Future<Unit>

  // TODO: enable some kind of interaction
}
