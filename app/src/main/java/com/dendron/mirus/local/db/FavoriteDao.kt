package com.dendron.mirus.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dendron.mirus.local.db.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<Favorite>>

    @Insert
    fun insert(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE movie_id = :movieId")
    fun delete(movieId: Int)

    @Query("SELECT 1 FROM favorite WHERE movie_id = :movieId")
    fun isFavorite(movieId: Int): Boolean
}