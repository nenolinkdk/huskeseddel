package com.nenolink.huskeseddel.data

import android.content.Context

class HuskeseddelRepository(context: Context, private val dao: HuskeseddelDao) {
    val customProducts = dao.observeCustomProducts()
    val shoppingItems = dao.observeShoppingItems()
    val listState = dao.observeListState()

    val defaultCategories: List<ProductCategory> by lazy {
        val json = context.assets.open("default_products.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        ProductCatalogParser.parse(json).categories
    }

    suspend fun addCustomProduct(name: String, categoryId: String) =
        dao.insertCustomProduct(CustomProductEntity(name = name.trim(), categoryId = categoryId))

    suspend fun updateCustomProduct(product: CustomProductEntity, name: String, categoryId: String) =
        dao.updateCustomProduct(product.copy(name = name.trim(), categoryId = categoryId))

    suspend fun deleteCustomProduct(product: CustomProductEntity) {
        dao.removeShoppingItem("custom:${product.id}")
        dao.deleteCustomProduct(product)
    }

    suspend fun select(product: Product) = dao.upsertShoppingItem(
        ShoppingItemEntity(
            productKey = product.key,
            displayName = product.name,
            categoryId = product.categoryId,
        ),
    )

    suspend fun deselect(productKey: String) = dao.removeShoppingItem(productKey)
    suspend fun updateShoppingItem(item: ShoppingItemEntity) = dao.upsertShoppingItem(item.copy(quantity = normalizedQuantity(item.quantity)))
    suspend fun clearPurchased() = dao.clearPurchased()
    suspend fun newShopping() = dao.newShopping()
    suspend fun saveNote(note: String) = dao.saveListState(ShoppingListStateEntity(note = note))
}
