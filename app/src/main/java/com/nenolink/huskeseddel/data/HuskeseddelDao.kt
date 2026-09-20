package com.nenolink.huskeseddel.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HuskeseddelDao {
    @Query("SELECT * FROM custom_products ORDER BY name COLLATE NOCASE")
    fun observeCustomProducts(): Flow<List<CustomProductEntity>>

    @Insert
    suspend fun insertCustomProduct(product: CustomProductEntity): Long

    @Update
    suspend fun updateCustomProduct(product: CustomProductEntity)

    @Delete
    suspend fun deleteCustomProduct(product: CustomProductEntity)

    @Query("SELECT * FROM shopping_items WHERE selected = 1 ORDER BY categoryId, displayName COLLATE NOCASE")
    fun observeShoppingItems(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertShoppingItem(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE productKey = :productKey")
    suspend fun removeShoppingItem(productKey: String)

    @Query("DELETE FROM shopping_items WHERE purchased = 1")
    suspend fun clearPurchased()

    @Query("DELETE FROM shopping_items")
    suspend fun clearShoppingItems()

    @Query("SELECT * FROM shopping_list_state WHERE id = 1")
    fun observeListState(): Flow<ShoppingListStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveListState(state: ShoppingListStateEntity)

    @Transaction
    suspend fun newShopping() {
        clearShoppingItems()
        saveListState(ShoppingListStateEntity(note = ""))
    }
}
