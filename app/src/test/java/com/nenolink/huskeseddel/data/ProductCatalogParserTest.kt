package com.nenolink.huskeseddel.data

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductCatalogParserTest {
    private fun catalog(): CatalogDocument {
        val source = File("src/main/assets/default_products.json").readText(Charsets.UTF_8)
        return ProductCatalogParser.parse(source)
    }

    @Test
    fun `existing default catalog is loaded with categories and Danish characters`() {
        val catalog = catalog()
        assertEquals(11, catalog.categories.size)
        assertTrue(catalog.categories.any { it.name == "Brød" })
        assertTrue(catalog.categories.flatMap { it.products }.any { it.name == "Mælk" })
    }

    @Test
    fun `search filters products across categories without case sensitivity`() {
        val result = filterCategories(catalog().categories, "RØD")
        val names = result.flatMap { it.products }.map { it.name }
        assertTrue("Røde vindruer" in names)
        assertTrue("Rødvin" in names)
        assertTrue(names.all { it.contains("rød", ignoreCase = true) })
    }

    @Test
    fun `empty search preserves all categories`() {
        val categories = catalog().categories
        assertEquals(categories, filterCategories(categories, "  "))
    }

    @Test
    fun `quantity never drops below one`() {
        assertEquals(1, normalizedQuantity(-4))
        assertEquals(1, normalizedQuantity(0))
        assertEquals(3, normalizedQuantity(3))
    }
}
