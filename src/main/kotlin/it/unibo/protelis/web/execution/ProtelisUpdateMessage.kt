package it.unibo.protelis.web.execution

/**
 * A Protelis Update Message is the sum-up of an execution step.
 * It propagates infos about the nodes.
 */
data class ProtelisUpdateMessage(
  /** A list of all the Nodes in the Protelis environment. */
  val nodes: List<ProtelisNode>,
  /** Dimensions of the environment of execution. */
  val envSize: Pair<Number, Number>
)
