package com.dendron.mirus.domain.repository

import com.dendron.mirus.domain.model.Movie

interface FavoriteMovieRepository {
    fun saveFavoriteMovie(movie: Movie)
    fun removeFavoriteMovie(movie: Movie)
    fun isFavoriteMovie(movie: Movie): Boolean
    fun getFavoritesMovie(): List<Int>
}