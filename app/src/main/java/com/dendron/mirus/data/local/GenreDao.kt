package com.dendron.mirus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dendron.mirus.data.local.model.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {

    @Query("SELECT * FROM genre")
    fun getGenres(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM genre WHERE id IN (:genres)")
    fun getGenres(vararg genres: Int): Flow<List<GenreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGenre(genreEntity: GenreEntity)

    @Query("DELETE FROM genre")
    fun deleteAll()
}