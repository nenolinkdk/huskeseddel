package com.nenolink.huskeseddel.data

import org.json.JSONObject

object ProductCatalogParser {
    fun parse(json: String): CatalogDocument {
        val categoriesJson = JSONObject(json).getJSONArray("categories")
        val categories = buildList {
            for (categoryIndex in 0 until categoriesJson.length()) {
                val categoryJson = categoriesJson.getJSONObject(categoryIndex)
                val categoryId = categoryJson.getString("id")
                val itemsJson = categoryJson.getJSONArray("items")
                val products = buildList {
                    for (itemIndex in 0 until itemsJson.length()) {
                        val item = itemsJson.getJSONObject(itemIndex)
                        add(Product(item.getString("id"), item.getString("name"), categoryId))
                    }
                }
                add(ProductCategory(categoryId, categoryJson.getString("name"), products))
            }
        }
        return CatalogDocument(categories)
    }
}
