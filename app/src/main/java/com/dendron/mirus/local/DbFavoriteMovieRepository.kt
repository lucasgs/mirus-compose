package com.dendron.mirus.local

import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import com.dendron.mirus.local.db.AppDatabase
import com.dendron.mirus.local.db.model.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DbFavoriteMovieRepository(private val appDatabase: AppDatabase) : FavoriteMovieRepository {

    override suspend fun saveFavoriteMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            appDatabase . favoriteDao ().insert(
                Favorite(
                    movieId = movie.id
                )
            )
        }
    }

    override suspend fun removeFavoriteMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            appDatabase.favoriteDao().getFavorites().firstOrNull { it.movieId == movie.id}?.let {
                appDatabase.favoriteDao().delete(it)
            }
        }
    }

    override suspend fun getFavoritesMovie(): List<Int> {
        return appDatabase.favoriteDao().getFavorites().map { it.movieId }
    }

    override suspend fun isFavoriteMovie(movie: Movie): Boolean {
        return appDatabase.favoriteDao().getFavorites().map { it.movieId }.contains(movie.id)
    }
}