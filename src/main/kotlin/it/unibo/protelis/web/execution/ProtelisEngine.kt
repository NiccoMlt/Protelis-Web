package it.unibo.protelis.web.execution

/** A ProtelisEngine object is some kind of connection with a simulator or actual network of devices. */
interface ProtelisEngine {

  /**
   * Build up a connection with some execution environment,
   * specifying the Protelis source code to execute and some kind of monitor.
   *
   * @param sourceCode the Protelis code to execute
   * @param monitor an observer object that monitors the execution and models the output strategy
   */
  fun setup(sourceCode: String, monitor: ProtelisObserver)

  /**
   * Start the execution of the code.
   *
   * @throws IllegalStateException if the connection was not set up (use [setup] method)
   */
  fun start()

  /**
   * Stop and tear down the connection.
   *
   * @throws IllegalStateException if the connection was not set up (use [setup] method)
   */
  fun stop()

  // TODO: enable some kind of interaction
}
