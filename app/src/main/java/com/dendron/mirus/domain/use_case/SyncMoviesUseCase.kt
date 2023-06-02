package com.dendron.mirus.domain.use_case

import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.NetworkChecker
import com.dendron.mirus.domain.repository.GenreRepository
import com.dendron.mirus.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SyncMoviesUseCase @Inject constructor(
    private val networkChecker: NetworkChecker,
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository,
) {
    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        runCatching {
            if (networkChecker.isOnline()) {
                movieRepository.syncMovies()
                genreRepository.syncMovieGenres()
            }
        }.onFailure {
            emit(Resource.Error(it.localizedMessage))
        }
    }
}