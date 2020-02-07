package it.unibo.protelis.web.execution

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.Json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ProtelisUpdateMessageTest {

  @Test
  @DisplayName("Test ProtelisUpdateMessage comparisons")
  fun `test ProtelisUpdateMessage`() {
    val id1 = "Pippo"
    val id2 = "Pluto"
    val coordinates1 = 0 to 1
    val coordinates2 = 1 to 0
    val n1 = ProtelisNode(id1, coordinates1)
    val n2 = ProtelisNode(id2, coordinates2)
    val ue = ProtelisUpdateMessage(listOf())
    val u1 = ProtelisUpdateMessage(listOf(n1))
    val u2 = ProtelisUpdateMessage(listOf(n1, n2))
    assertEquals(ProtelisUpdateMessage(listOf(n1, n2)), u2)
    assertEquals(ProtelisUpdateMessage(listOf()), ue)
    assertNotEquals(ProtelisUpdateMessage(listOf(n2)), u1)
    assertNotEquals(ue, u1)
    assertEquals(ProtelisUpdateMessage(listOf(n1)).hashCode(), u1.hashCode())
  }

  @Test
  @DisplayName("Test ProtelisUpdateMessage serialization with Jackson")
  fun `test JSON serialization`() {
    val id1 = "Pippo"
    val id2 = "Pluto"
    val coordinates1 = 0 to 1
    val coordinates2 = 1 to 0
    val node1 = ProtelisNode(id1, coordinates1)
    val node2 = ProtelisNode(id2, coordinates2)
    val message = ProtelisUpdateMessage(listOf(node1, node2))
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
        ]
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
