package com.nenolink.huskeseddel.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nenolink.huskeseddel.data.CustomProductEntity
import com.nenolink.huskeseddel.data.HuskeseddelDatabase
import com.nenolink.huskeseddel.data.HuskeseddelRepository
import com.nenolink.huskeseddel.data.Product
import com.nenolink.huskeseddel.data.ProductCategory
import com.nenolink.huskeseddel.data.ShoppingItemEntity
import com.nenolink.huskeseddel.data.filterCategories
import com.nenolink.huskeseddel.data.normalizedQuantity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HuskeseddelUiState(
    val categories: List<ProductCategory> = emptyList(),
    val allCategories: List<ProductCategory> = emptyList(),
    val customProducts: List<CustomProductEntity> = emptyList(),
    val shoppingItems: List<ShoppingItemEntity> = emptyList(),
    val note: String = "",
    val query: String = "",
)

class HuskeseddelViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HuskeseddelRepository(application, HuskeseddelDatabase.get(application).dao())
    private val query = MutableStateFlow("")
    private val defaults = MutableStateFlow(repository.defaultCategories)

    val uiState = combine(
        defaults,
        repository.customProducts,
        repository.shoppingItems,
        repository.listState,
        query,
    ) { defaultCategories, custom, shopping, listState, search ->
        val customByCategory = custom.groupBy { it.categoryId }
        val known = defaultCategories.map { it.id }.toSet()
        val categories = defaultCategories.map { category ->
            category.copy(products = category.products + customByCategory[category.id].orEmpty().map { it.toProduct() })
        }.toMutableList()
        val uncategorized = custom.filter { it.categoryId !in known }
        if (uncategorized.isNotEmpty()) {
            categories += ProductCategory("egne_varer", "Egne varer", uncategorized.map { it.toProduct() })
        }
        HuskeseddelUiState(
            categories = filterCategories(categories, search),
            allCategories = categories,
            customProducts = custom,
            shoppingItems = shopping,
            note = listState?.note.orEmpty(),
            query = search,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HuskeseddelUiState())

    fun setQuery(value: String) { query.value = value }

    fun setSelected(product: Product, selected: Boolean) = io {
        if (selected) repository.select(product) else repository.deselect(product.key)
    }

    fun setPurchased(item: ShoppingItemEntity, purchased: Boolean) = io {
        repository.updateShoppingItem(item.copy(purchased = purchased))
    }

    fun changeQuantity(item: ShoppingItemEntity, delta: Int) = io {
        repository.updateShoppingItem(item.copy(quantity = normalizedQuantity(item.quantity + delta)))
    }

    fun saveNote(note: String) = io { repository.saveNote(note) }
    fun clearPurchased() = io { repository.clearPurchased() }
    fun newShopping() = io { repository.newShopping() }

    fun saveCustom(existing: CustomProductEntity?, name: String, categoryId: String) = io {
        if (name.isBlank()) return@io
        if (existing == null) repository.addCustomProduct(name, categoryId)
        else repository.updateCustomProduct(existing, name, categoryId)
    }

    fun deleteCustom(product: CustomProductEntity) = io { repository.deleteCustomProduct(product) }

    private fun io(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) { block() }
    }
}

private fun CustomProductEntity.toProduct() = Product("custom:$id", name, categoryId, id)
