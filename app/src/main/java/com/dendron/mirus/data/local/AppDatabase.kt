package com.dendron.mirus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dendron.mirus.data.local.model.FavoriteEntity

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}