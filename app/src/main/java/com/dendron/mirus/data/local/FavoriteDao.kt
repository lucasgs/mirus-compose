package com.dendron.mirus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.dendron.mirus.data.local.model.FavoriteEntity
import com.dendron.mirus.data.local.model.FavoriteMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Transaction
    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<FavoriteMovie>>

    @Insert
    fun insert(favoriteEntity: FavoriteEntity)

    @Query("DELETE FROM favorite WHERE movieId = :movieId")
    fun delete(movieId: Int)

    @Query("SELECT 1 FROM favorite WHERE movieId = :movieId")
    fun isFavorite(movieId: Int): Boolean
}