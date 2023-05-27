package com.dendron.mirus.data.repository

import com.dendron.mirus.data.local.GenreDao
import com.dendron.mirus.data.local.model.GenreEntity
import com.dendron.mirus.data.remote.TheMovieDBApi
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.repository.GenreRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenreRepositoryImp @Inject constructor(
    private val api: TheMovieDBApi,
    private val genreDao: GenreDao
) :
    GenreRepository {
    override suspend fun getGenreDetails(genresId: List<Int>): Flow<List<Genre>> =
        genreDao.getGenres().map { genres -> genres.map { genre -> genre.toDomain() } }

    override suspend fun getGenres(): Flow<List<Genre>> = flow {
        withContext(IO) {
            syncMovieGenres()
        }
        emitAll(genreDao.getGenres().map { genres -> genres.map { genre -> genre.toDomain() } })
    }

    private suspend fun syncMovieGenres() {
        runCatching {
            api.getMovieGenres()
        }.onSuccess { result ->
            withContext(IO) {
                val delete = async { genreDao.deleteAll() }
                delete.join()
                result.genreDtos.forEach { genre ->
                    genreDao.insertGenre(genre.toEntity())
                }
            }
        }
    }
}

private fun GenreEntity.toDomain(): Genre = Genre(
    id = id, name = name
)