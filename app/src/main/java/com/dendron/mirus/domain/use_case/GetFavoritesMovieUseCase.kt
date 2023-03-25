package com.dendron.mirus.domain.use_case

import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetFavoritesMovieUseCase @Inject constructor(private val favoriteMovieRepository: FavoriteMovieRepository) {
    operator fun invoke(): Flow<List<Movie>> = flow {
        emitAll(
            favoriteMovieRepository.getFavoritesMovie()
        )
    }.flowOn(Dispatchers.IO)
}