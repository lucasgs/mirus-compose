package com.dendron.mirus.domain.repository

interface FavoriteMovieStore {
    fun saveMovie(data: String)
    fun getMovies(): String
}

