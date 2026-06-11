package com.dendron.mirus.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
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

    @Query("SELECT * FROM movie WHERE title LIKE '%' || :query || '%'")
    fun searchMovies(query: String): Flow<List<MovieEntity>>

    @Transaction
    @Query("SELECT * FROM discovery")
    fun getDiscoveryMovies(): Flow<List<DiscoveryMovie>>

    @Upsert
    suspend fun upsertMovie(movieEntity: MovieEntity)

    @Upsert
    suspend fun upsertMovies(movieEntities: List<MovieEntity>)

    @Upsert
    suspend fun upsertDiscovery(discoveryEntities: List<DiscoveryEntity>)

    @Query("DELETE FROM discovery")
    suspend fun clearDiscovery()

    @Transaction
    @Query("SELECT * FROM top_rated")
    fun getTopRatedMovies(): Flow<List<TopRatedMovie>>

    @Upsert
    suspend fun upsertTopRated(topRatedEntities: List<TopRatedEntity>)

    @Query("DELETE FROM top_rated")
    suspend fun clearTopRated()

    @Transaction
    @Query("SELECT * FROM trending")
    fun getTrendingMovies(): Flow<List<TrendingMovie>>

    @Upsert
    suspend fun upsertTrending(trendingEntities: List<TrendingEntity>)

    @Query("DELETE FROM trending")
    suspend fun clearTrending()

    @Query("DELETE FROM movie WHERE id = :movieId")
    suspend fun delete(movieId: Int)

    @Query("SELECT * FROM movie WHERE id = :movieId")
    suspend fun getMovieDetail(movieId: Int): MovieEntity
}