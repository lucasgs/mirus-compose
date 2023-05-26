package com.dendron.mirus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dendron.mirus.data.local.model.DiscoveryEntity
import com.dendron.mirus.data.local.model.DiscoveryMovie
import com.dendron.mirus.data.local.model.MovieEntity
import com.dendron.mirus.data.local.model.TopRatedEntity
import com.dendron.mirus.data.local.model.TopRatedMovie
import com.dendron.mirus.data.local.model.TrendingEntity
import com.dendron.mirus.data.local.model.TrendingMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie")
    fun getMovies(): Flow<List<MovieEntity>>

    @Transaction
    @Query("SELECT * FROM discovery")
    fun getDiscoveryMovies(): Flow<List<DiscoveryMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movieEntity: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiscovery(discoveryEntity: DiscoveryEntity)


    @Transaction
    @Query("SELECT * FROM top_rated")
    fun getTopRatedMovies(): Flow<List<TopRatedMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopRated(topRatedEntity: TopRatedEntity)

    @Transaction
    @Query("SELECT * FROM trending")
    fun getTrendingMovies(): Flow<List<TrendingMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrending(trendingEntity: TrendingEntity)

    @Query("DELETE FROM movie WHERE id = :movieId")
    fun delete(movieId: Int)

    @Query("SELECT * FROM movie WHERE id = :movieId")
    suspend fun getMovieDetail(movieId: Int): MovieEntity
}