package com.dendron.mirus.domain.repository

import com.dendron.mirus.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface FavoriteMovieRepository {
    suspend fun saveFavoriteMovie(movie: Movie)
    suspend fun removeFavoriteMovie(movie: Movie)
    suspend fun isFavoriteMovie(movie: Movie): Boolean
    suspend fun getFavoritesMovie(): Flow<List<Movie>>
}