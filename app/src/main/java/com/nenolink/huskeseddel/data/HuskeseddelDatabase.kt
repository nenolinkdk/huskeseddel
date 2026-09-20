package com.nenolink.huskeseddel.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CustomProductEntity::class, ShoppingItemEntity::class, ShoppingListStateEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class HuskeseddelDatabase : RoomDatabase() {
    abstract fun dao(): HuskeseddelDao

    companion object {
        @Volatile private var instance: HuskeseddelDatabase? = null

        fun get(context: Context): HuskeseddelDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                HuskeseddelDatabase::class.java,
                "huskeseddel.db",
            ).build().also { instance = it }
        }
    }
}
