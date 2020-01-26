package it.unibo.protelis.web.execution

/** A Protelis Node info object encapsulates all the infos about a Node in the Protelis environment. */
data class ProtelisNode(
  /** The ID of the node */
  val id: String,

  /** The 2D coordinates of the node */
  val coordinates: Pair<Number, Number>
)
