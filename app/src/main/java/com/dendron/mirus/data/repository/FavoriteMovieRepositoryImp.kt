package com.dendron.mirus.data.repository

import com.dendron.mirus.data.local.FavoriteDao
import com.dendron.mirus.data.local.model.FavoriteEntity
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteMovieRepositoryImp(private val favoriteDao: FavoriteDao) : FavoriteMovieRepository {

    override suspend fun saveFavoriteMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            favoriteDao.insert(
                FavoriteEntity(
                    movieId = movie.id
                )
            )
        }
    }

    override suspend fun removeFavoriteMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            favoriteDao.delete(movie.id)
        }
    }

    override suspend fun getFavoriteMovies(): Flow<List<Movie>> =
        favoriteDao.getFavorites().map { favorites ->
            favorites.map { it.movie.toDomain() }
        }

    override suspend fun isFavoriteMovie(movie: Movie): Boolean {
        return favoriteDao.isFavorite(movie.id)
    }
}