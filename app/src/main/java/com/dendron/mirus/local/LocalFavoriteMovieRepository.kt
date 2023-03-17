package com.dendron.mirus.local

import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import com.dendron.mirus.domain.repository.FavoriteMovieStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalFavoriteMovieRepository(private val localStore: FavoriteMovieStore) :
    FavoriteMovieRepository {

    private val gson = Gson()
    private val moviesType = object : TypeToken<List<Int>>() {}.type

    override suspend fun saveFavoriteMovie(movie: Movie) {
        val movies = getFavoritesMovie().toMutableList()
        if (!movies.any { id -> id == movie.id }) {
            movies.add(movie.id)
            val data = gson.toJson(movies)
            localStore.saveMovie(data)
        }
    }

    override suspend fun removeFavoriteMovie(movie: Movie) {
        val movies = getFavoritesMovie().toMutableList()
        if (movies.any { id -> id == movie.id }) {
            movies.remove(movie.id)
            val data = gson.toJson(movies)
            localStore.saveMovie(data)
        }
    }

    override suspend fun getFavoritesMovie(): List<Int> {
        val data = localStore.getMovies()
        if (data.isEmpty()) return emptyList()
        return gson.fromJson(data, moviesType)
    }

    override suspend fun isFavoriteMovie(movie: Movie): Boolean {
        return getFavoritesMovie().any { it == movie.id }
    }
}