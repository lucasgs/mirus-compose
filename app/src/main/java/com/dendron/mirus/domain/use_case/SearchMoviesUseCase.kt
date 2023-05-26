package com.dendron.mirus.domain.use_case

import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(query: String): Flow<Resource<List<Movie>>> = flow {
        runCatching {
            emit(Resource.Loading())
            val movies = movieRepository.searchMovies(query)
            emit(Resource.Success(movies))
        }.onFailure {
            emit(Resource.Error(it.localizedMessage))
        }
    }
}