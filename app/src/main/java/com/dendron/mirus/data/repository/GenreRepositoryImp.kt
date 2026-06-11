package com.dendron.mirus.data.repository

import androidx.room.withTransaction
import com.dendron.mirus.data.local.AppDatabase
import com.dendron.mirus.data.local.model.GenreEntity
import com.dendron.mirus.data.remote.TheMovieDBApi
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.repository.GenreRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenreRepositoryImp @Inject constructor(
    private val api: TheMovieDBApi,
    private val appDatabase: AppDatabase,
) :
    GenreRepository {

    override fun getGenreDetails(genresId: List<Int>): Flow<List<Genre>> =
        appDatabase.genreDao().getGenres()
            .map { genres ->
                genres
                    .filter { genre -> genre.id in genresId }
                    .map { genre -> genre.toDomain() }
            }

    override fun getGenres(): Flow<List<Genre>> =
        appDatabase.genreDao().getGenres()
            .map { genres -> genres.map { genre -> genre.toDomain() } }

    override suspend fun syncMovieGenres() {
        runCatching {
            api.getMovieGenres()
        }.onSuccess { result ->
            withContext(IO) {
                appDatabase.withTransaction {
                    appDatabase.genreDao().run {
                        deleteAll()
                        result.genreDtos.forEach { genre ->
                            insertGenre(genre.toEntity())
                        }
                    }
                }
            }
        }
    }
}

private fun GenreEntity.toDomain(): Genre = Genre(
    id = id, name = name
)