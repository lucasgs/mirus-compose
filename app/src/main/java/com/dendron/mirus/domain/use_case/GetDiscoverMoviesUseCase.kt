package com.dendron.mirus.domain.use_case

import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDiscoverMoviesUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<Resource<List<Movie>>> = flow {
        kotlin.runCatching {
            emit(Resource.Loading())
            emitAll(movieRepository.getDiscoverMovies().map { Resource.Success(it) })
        }.onFailure {
            emit(Resource.Error(it.localizedMessage))
        }
    }
}