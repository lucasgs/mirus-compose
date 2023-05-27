package com.dendron.mirus.domain.repository

import com.dendron.mirus.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    suspend fun getGenreDetails(genresId: List<Int>): Flow<List<Genre>>
    suspend fun getGenres(): Flow<List<Genre>>
}