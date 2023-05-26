package com.dendron.mirus.domain.use_case

import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ToggleMovieFavoriteUseCase @Inject constructor(private val favoriteMovieRepository: FavoriteMovieRepository) {
    operator fun invoke(movie: Movie, isFavorite: Boolean): Flow<Resource<Boolean>> = flow {
        runCatching {
            if (isFavorite) {
                favoriteMovieRepository.removeFavoriteMovie(movie)
            } else {
                favoriteMovieRepository.saveFavoriteMovie(movie)
            }
            emit(Resource.Success(true))
        }.onFailure {
            emit(Resource.Error(it.localizedMessage))
        }
    }
}