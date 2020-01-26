package it.unibo.protelis.web.execution

/** A Protelis Observer is a consumer of update messages from a Protelis Engine. */
interface ProtelisObserver {

  /**
   * This method will be called by the engine as soon as the initialization phase is completed.
   *
   * @param update the update infos
   */
  fun initialized(update: ProtelisUpdateMessage)

  /**
   * This method will be called by the simulation every time a simulation step is completed.
   *
   * @param update the update infos
   */
  fun stepDone(update: ProtelisUpdateMessage)

  /**
   * This method will be called by the simulation once the whole simulation has finished,
   * either because it reached its latest point or because the user stopped it.
   *
   * @param update the update infos
   */
  fun finished(update: ProtelisUpdateMessage)
}
