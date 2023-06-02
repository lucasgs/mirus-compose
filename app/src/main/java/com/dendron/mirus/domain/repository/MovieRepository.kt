package com.dendron.mirus.domain.repository

import com.dendron.mirus.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getDiscoverMovies(): Flow<List<Movie>>
    suspend fun getTopRatedMovies(): Flow<List<Movie>>
    suspend fun getTrendingMovies(): Flow<List<Movie>>
    suspend fun getMovieDetails(movieId: String): Flow<Movie>
    suspend fun searchMovies(query: String): Flow<List<Movie>>
    suspend fun syncMovies()
}
