package com.nenolink.huskeseddel.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ShoppingListShareTextTest {
    private val categories = mapOf(
        "brod" to "Brød",
        "mejeri" to "Mejeri",
    )

    @Test
    fun `share text includes categories names quantities and note`() {
        val items = listOf(
            ShoppingItemEntity("p1", "Rugbrød", "brod", quantity = 2),
            ShoppingItemEntity("p2", "Mælk", "mejeri", quantity = 1),
            ShoppingItemEntity("p3", "Franskbrød", "brod", quantity = 1),
        )

        val text = formatShoppingListText(items, categories, "  Husk økologisk  ")

        assertEquals(
            """
            Brød
            Rugbrød × 2
            Franskbrød × 1

            Mejeri
            Mælk × 1

            Note
            Husk økologisk
            """.trimIndent(),
            text,
        )
    }

    @Test
    fun `unknown category falls back to egne varer`() {
        val items = listOf(ShoppingItemEntity("custom:1", "Hjemmelavet pesto", "andetet"))
        val text = formatShoppingListText(items, categories, "")
        assertEquals(
            """
            Egne varer
            Hjemmelavet pesto × 1
            """.trimIndent(),
            text,
        )
    }

    @Test
    fun `empty list can still share a note`() {
        val text = formatShoppingListText(emptyList(), categories, "Kun note")
        assertEquals(
            """
            Note
            Kun note
            """.trimIndent(),
            text,
        )
    }

    @Test
    fun `share text does not include purchased state`() {
        val items = listOf(
            ShoppingItemEntity("p1", "Mælk", "mejeri", purchased = true, quantity = 3),
        )
        val text = formatShoppingListText(items, categories, "")
        assertEquals(
            """
            Mejeri
            Mælk × 3
            """.trimIndent(),
            text,
        )
        assertFalse(text.contains("købt", ignoreCase = true))
    }
}
