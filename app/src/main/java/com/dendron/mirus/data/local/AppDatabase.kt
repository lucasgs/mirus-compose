package com.dendron.mirus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dendron.mirus.data.local.model.FavoriteEntity
import com.dendron.mirus.data.local.model.GenreEntity
import com.dendron.mirus.data.local.model.MovieEntity

@Database(entities = [FavoriteEntity::class, MovieEntity::class, GenreEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun movieDao(): MovieDao
}