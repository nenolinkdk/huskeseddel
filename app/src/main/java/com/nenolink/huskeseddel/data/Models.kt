package com.nenolink.huskeseddel.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Product(val key: String, val name: String, val categoryId: String, val customId: Long? = null)
data class ProductCategory(val id: String, val name: String, val products: List<Product>)

@Entity(tableName = "custom_products")
data class CustomProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey val productKey: String,
    val displayName: String,
    val categoryId: String,
    val selected: Boolean = true,
    val purchased: Boolean = false,
    val quantity: Int = 1,
)

@Entity(tableName = "shopping_list_state")
data class ShoppingListStateEntity(
    @PrimaryKey val id: Int = 1,
    val note: String = "",
)

data class CatalogDocument(val categories: List<ProductCategory>)

fun normalizedQuantity(value: Int): Int = value.coerceAtLeast(1)

fun filterCategories(categories: List<ProductCategory>, query: String): List<ProductCategory> {
    val term = query.trim()
    if (term.isEmpty()) return categories
    return categories.mapNotNull { category ->
        val matches = category.products.filter { it.name.contains(term, ignoreCase = true) }
        category.copy(products = matches).takeIf { matches.isNotEmpty() }
    }
}
