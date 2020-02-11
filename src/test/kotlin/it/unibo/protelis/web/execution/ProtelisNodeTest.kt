package it.unibo.protelis.web.execution

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ProtelisNodeTest {

  @Test
  @DisplayName("Test ProtelisNode comparisons")
  fun `test ProtelisNode`() {
    val id = "Pippo"
    val coordinates = 0 to 1
    val n = ProtelisNode(id, coordinates)
    assertNotNull(n)
    assertEquals(ProtelisNode(id, coordinates), n)
    assertNotEquals(ProtelisNode("Pluto", coordinates), n)
    assertNotEquals(ProtelisNode(id, 0 to 0), n)
    assertEquals(ProtelisNode(id, coordinates).hashCode(), n.hashCode())
  }

  @Test
  @DisplayName("Test ProtelisNode serialization with Jackson")
  fun `test JSON serialization`() {
    val id = "Pippo"
    val coordinates = 0 to 0
    val node = ProtelisNode(id, coordinates)
    val jsonNode = """
      {
        "id": "$id",
        "coordinates": {
          "first": ${coordinates.first},
          "second": ${coordinates.second}
        }
      }
    """.trimIndent()
    val mapper = jacksonObjectMapper()
    val jacksonNode = mapper.writeValueAsString(node)
    assertEquals(Json.decodeValue(jsonNode), Json.decodeValue(jacksonNode))
    assertEquals(
      mapper.readValue(jsonNode, ProtelisNode::class.java),
      mapper.readValue(jacksonNode, ProtelisNode::class.java)
    )
  }
}
