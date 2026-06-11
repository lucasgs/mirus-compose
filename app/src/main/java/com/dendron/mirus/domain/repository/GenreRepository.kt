package com.dendron.mirus.domain.repository

import com.dendron.mirus.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    fun getGenreDetails(genresId: List<Int>): Flow<List<Genre>>
    fun getGenres(): Flow<List<Genre>>
    suspend fun syncMovieGenres()
}