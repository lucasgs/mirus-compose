package com.dendron.mirus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dendron.mirus.data.local.model.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Insert
    fun insert(favoriteEntity: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE movie_id = :movieId")
    fun delete(movieId: Int)

    @Query("SELECT 1 FROM favorite WHERE movie_id = :movieId")
    fun isFavorite(movieId: Int): Boolean
}