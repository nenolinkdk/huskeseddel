package com.nenolink.huskeseddel.data

fun formatShoppingListText(
    items: List<ShoppingItemEntity>,
    categoryNames: Map<String, String>,
    note: String,
): String {
    return buildString {
        items.groupBy { it.categoryId }.forEach { (categoryId, entries) ->
            if (isNotEmpty()) appendLine()
            appendLine(categoryNames[categoryId] ?: "Egne varer")
            entries.forEach { item ->
                appendLine("${item.displayName} × ${item.quantity}")
            }
        }
        val trimmedNote = note.trim()
        if (trimmedNote.isNotEmpty()) {
            if (isNotEmpty()) appendLine()
            appendLine("Note")
            appendLine(trimmedNote)
        }
    }.trimEnd()
}
