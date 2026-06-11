package com.dendron.mirus.domain.use_case

import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoritesMovieUseCase @Inject constructor(
    private val favoriteMovieRepository: FavoriteMovieRepository
) {
    operator fun invoke(): Flow<Resource<List<Movie>>> = flow {
        runCatching {
            emit(Resource.Loading())
            emitAll(favoriteMovieRepository.getFavoriteMovies().map { Resource.Success(it) })
        }.onFailure {
            emit(Resource.Error(it.localizedMessage))
        }
    }
}
