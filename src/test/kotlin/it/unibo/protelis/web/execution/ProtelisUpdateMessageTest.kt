package it.unibo.protelis.web.execution

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/** Test the ProtelisUpdateMessage data class. */
class ProtelisUpdateMessageTest {

  /** Test that objects are equal when they should be and different when they should not. */
  @Test
  @DisplayName("Test ProtelisUpdateMessage comparisons")
  fun `test ProtelisUpdateMessage`() {
    val id1 = "Pippo"
    val id2 = "Pluto"
    val coordinates1 = 0 to 1
    val coordinates2 = 1 to 0
    val env: Pair<Int, Int> = 2 to 2
    val n1 = ProtelisNode(id1, coordinates1)
    val n2 = ProtelisNode(id2, coordinates2)
    val ue = ProtelisUpdateMessage(listOf(), env)
    val u1 = ProtelisUpdateMessage(listOf(n1), env)
    val u2 = ProtelisUpdateMessage(listOf(n1, n2), env)
    assertEquals(ProtelisUpdateMessage(listOf(n1, n2), env), u2)
    assertEquals(ProtelisUpdateMessage(listOf(), 2 to 2), ue)
    assertNotEquals(ProtelisUpdateMessage(listOf(n2), env), u1)
    assertNotEquals(ue, u1)
    assertEquals(ProtelisUpdateMessage(listOf(n1), 2 to 2).hashCode(), u1.hashCode())
  }

  /** Test that Jackson (and then Vert.x) serializes the object in JSON as expected. */
  @Test
  @DisplayName("Test ProtelisUpdateMessage serialization with Jackson")
  fun `test JSON serialization`() {
    val id1 = "Pippo"
    val id2 = "Pluto"
    val coordinates1 = 0 to 1
    val coordinates2 = 1 to 0
    val env: Pair<Int, Int> = 2 to 2
    val node1 = ProtelisNode(id1, coordinates1)
    val node2 = ProtelisNode(id2, coordinates2)
    val message = ProtelisUpdateMessage(listOf(node1, node2), env)
    val jsonNode = """
      {
        "nodes": [
          {
            "id": "$id1",
            "coordinates": {
              "first": ${coordinates1.first},
              "second": ${coordinates1.second}
            }
          },
          {
            "id": "$id2",
            "coordinates": {
              "first": ${coordinates2.first},
              "second": ${coordinates2.second}
            }
          }
        ],
        "envSize": {
          "first": ${env.first},
          "second": ${env.second}
        }
      }
    """.trimIndent()
    val mapper = jacksonObjectMapper()
    val jacksonNode = mapper.writeValueAsString(message)
    assertEquals(Json.decodeValue(jsonNode), Json.decodeValue(jacksonNode))
    assertEquals(
      mapper.readValue(jsonNode, ProtelisUpdateMessage::class.java),
      mapper.readValue(jacksonNode, ProtelisUpdateMessage::class.java)
    )
  }
}
