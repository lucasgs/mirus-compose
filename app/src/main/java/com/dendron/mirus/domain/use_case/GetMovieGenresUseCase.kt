package com.dendron.mirus.domain.use_case

import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.repository.GenreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMovieGenresUseCase @Inject constructor(private val genreRepository: GenreRepository) {
    operator fun invoke(): Flow<Resource<List<Genre>>> = flow {
        runCatching {
            emit(Resource.Loading())
            emitAll(genreRepository.getGenres().map { Resource.Success(it) })
        }.onFailure {
            emit(Resource.Error(it.localizedMessage))
        }
    }
}