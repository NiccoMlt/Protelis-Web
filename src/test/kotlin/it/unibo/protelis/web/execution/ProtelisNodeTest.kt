package it.unibo.protelis.web.execution

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/** Test the ProtelisNode data class. */
class ProtelisNodeTest {

  /** Test that objects are equal when they should be and different when they should not. */
  @Test
  @DisplayName("Test ProtelisNode comparisons")
  fun `test ProtelisNode`() {
    val id = "Pippo"
    val value = "foo"
    val coordinates = 0 to 1
    val n = ProtelisNode(id, coordinates, value)
    assertNotNull(n)
    assertEquals(ProtelisNode(id, coordinates, value), n)
    assertNotEquals(ProtelisNode("Pluto", coordinates, value), n)
    assertNotEquals(ProtelisNode(id, 0 to 0, value), n)
    assertEquals(ProtelisNode(id, coordinates, value).hashCode(), n.hashCode())
  }

  /** Test that Jackson (and then Vert.x) serializes the object in JSON as expected. */
  @Test
  @DisplayName("Test ProtelisNode serialization with Jackson")
  fun `test JSON serialization`() {
    val id = "Pippo"
    val value = "foo"
    val coordinates = 0 to 0
    val node = ProtelisNode(id, coordinates, value)
    val jsonNode = """
      {
        "id": "$id",
        "coordinates": {
          "first": ${coordinates.first},
          "second": ${coordinates.second}
        },
        "value": "$value"
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
