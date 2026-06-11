package com.dendron.mirus.domain.repository

import com.dendron.mirus.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getDiscoverMovies(): Flow<List<Movie>>
    fun getTopRatedMovies(): Flow<List<Movie>>
    fun getTrendingMovies(): Flow<List<Movie>>
    fun getMovieDetails(movieId: String): Flow<Movie>
    fun searchMovies(query: String): Flow<List<Movie>>
    suspend fun syncMovies()
}
