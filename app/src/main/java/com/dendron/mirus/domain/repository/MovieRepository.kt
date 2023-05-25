package com.dendron.mirus.domain.repository

import com.dendron.mirus.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getDiscoverMovies(): Flow<List<Movie>>
    suspend fun getTopRatedMovies(): List<Movie>
    suspend fun getTrendingMovies(): List<Movie>
    suspend fun getMovieDetails(movieId: String): Movie
    suspend fun searchMovies(query: String): List<Movie>
}
