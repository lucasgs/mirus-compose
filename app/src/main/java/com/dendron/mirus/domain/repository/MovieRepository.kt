package com.dendron.mirus.domain.repository

import com.dendron.mirus.domain.model.Movie

interface MovieRepository {
    suspend fun getDiscoverMovies(): List<Movie>
    suspend fun getMovieDetails(movieId: String): Movie
    suspend fun searchMovies(query: String): List<Movie>
}
