package com.dendron.mirus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dendron.mirus.data.local.model.DiscoveryEntity
import com.dendron.mirus.data.local.model.FavoriteEntity
import com.dendron.mirus.data.local.model.GenreEntity
import com.dendron.mirus.data.local.model.MovieEntity
import com.dendron.mirus.data.local.model.TopRatedEntity
import com.dendron.mirus.data.local.model.TrendingEntity

@Database(
    entities = [FavoriteEntity::class, MovieEntity::class, GenreEntity::class, DiscoveryEntity::class, TopRatedEntity::class, TrendingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun movieDao(): MovieDao
    abstract fun genreDao(): GenreDao
}