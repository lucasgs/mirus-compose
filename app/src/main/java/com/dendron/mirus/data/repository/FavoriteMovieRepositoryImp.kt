package com.dendron.mirus.data.repository

import com.dendron.mirus.data.local.AppDatabase
import com.dendron.mirus.data.local.model.FavoriteEntity
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteMovieRepositoryImp(private val appDatabase: AppDatabase) : FavoriteMovieRepository {

    override suspend fun saveFavoriteMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            appDatabase.favoriteDao().insert(
                FavoriteEntity(
                    movieId = movie.id
                )
            )
        }
    }

    override suspend fun removeFavoriteMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            appDatabase.favoriteDao().delete(movie.id)
        }
    }

    override suspend fun getFavoritesMovie(): Flow<List<Movie>> =
        appDatabase.favoriteDao().getFavorites().map { favorites ->
            favorites.map { it.movie.toDomain() }
        }

    override suspend fun isFavoriteMovie(movie: Movie): Boolean {
        return appDatabase.favoriteDao().isFavorite(movie.id)
    }
}