package com.dendron.mirus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dendron.mirus.data.local.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie")
    fun getMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movieEntity: MovieEntity)

    @Query("DELETE FROM movie WHERE id = :movieId")
    fun delete(movieId: Int)
}