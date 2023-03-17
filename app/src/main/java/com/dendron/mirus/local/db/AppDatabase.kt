package com.dendron.mirus.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dendron.mirus.local.db.model.Favorite

@Database(entities = [Favorite::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}