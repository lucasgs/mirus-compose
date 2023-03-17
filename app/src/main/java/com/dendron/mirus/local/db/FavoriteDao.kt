package com.dendron.mirus.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dendron.mirus.local.db.model.Favorite

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    suspend fun getFavorites(): List<Favorite>

    @Insert
    fun insert(favorite: Favorite)

    @Delete
    fun delete(favorite: Favorite)
}