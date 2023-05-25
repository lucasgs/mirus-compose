package com.dendron.mirus.data.repository

import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import com.dendron.mirus.data.local.AppDatabase
import com.dendron.mirus.data.local.model.FavoriteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class FavoriteMovieRepositoryImp(private val appDatabase: AppDatabase) : FavoriteMovieRepository {

    override suspend fun saveFavoriteMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            appDatabase.favoriteDao().insert(
                FavoriteEntity(
                    id = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
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
            favorites.map {
                Movie(
                    id = it.id,
                    title = it.title,
                    posterPath = it.posterPath,
                    overview = "",
                    popularity = 0.0,
                    releaseDate = "",
                    backDropPath = "",
                    voteAverage = 0.0,
                    genres = emptyList()
                )
            }
        }

    override suspend fun isFavoriteMovie(movie: Movie): Boolean {
        return appDatabase.favoriteDao().isFavorite(movie.id)
    }
}