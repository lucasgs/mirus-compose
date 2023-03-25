package com.dendron.mirus.local

import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import com.dendron.mirus.local.db.AppDatabase
import com.dendron.mirus.local.db.model.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class DbFavoriteMovieRepository(private val appDatabase: AppDatabase) : FavoriteMovieRepository {

    override suspend fun saveFavoriteMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            appDatabase.favoriteDao().insert(
                Favorite(
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