package com.dendron.mirus.data.repository

import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.data.local.AppDatabase
import com.dendron.mirus.data.local.GenreDao
import com.dendron.mirus.data.local.model.GenreEntity
import com.dendron.mirus.data.remote.TheMovieDBApi
import com.dendron.mirus.data.remote.dto.GenreDto
import com.dendron.mirus.data.remote.dto.GenreResultDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryImpTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var api: TheMovieDBApi

    @Mock
    private lateinit var genreDao: GenreDao

    @Mock
    private lateinit var mockAppDatabase: AppDatabase

    private lateinit var genreRepositoryImp: GenreRepositoryImp

    @Before
    fun setUp() {
        genreRepositoryImp = GenreRepositoryImp(api, mockAppDatabase)

        whenever(mockAppDatabase.genreDao()).thenReturn(
            genreDao
        )
    }

    @Test
    fun `getGenreDetails should return empty data if api returns empty`() = runTest {

        whenever(genreDao.getGenres()).thenReturn(
            flowOf(
                emptyList()
            )
        )

        val result = genreRepositoryImp.getGenreDetails(genreIds).first()
        assert(result.isEmpty())
        verify(genreDao).getGenres()
    }

    @Test
    fun `getGenreDetails should return data if dao does return data`() = runTest {

        whenever(genreDao.getGenres()).thenReturn(
            flowOf(genreEntities)
        )

        val genres = genreRepositoryImp.getGenreDetails(genreIds).first()
        assert(genres.size == genreEntities.size)
        verify(genreDao).getGenres()
    }

    @Test
    fun `getGenres should sync api data with local storage`() = runTest {

        whenever(mockAppDatabase.genreDao()).thenReturn(
            genreDao
        )
        whenever(genreDao.getGenres()).thenReturn(
            flowOf(genreEntities)
        )

        val genres = genreRepositoryImp.getGenres().first()
        assert(genres.size == genreDtos.size)
    }

    companion object {

        val genreIds = listOf(1, 2, 3)

        val genreEntities = listOf(
            GenreEntity(
                id = 1,
                name = "Genre1"
            ),
            GenreEntity(
                id = 2,
                name = "Genre2"
            ),
            GenreEntity(
                id = 3,
                name = "Genre3"
            )
        )

        private val genreDtos = listOf(
            GenreDto(id = 1, name = "Genre1"),
            GenreDto(id = 2, name = "Genre2"),
            GenreDto(id = 3, name = "Genre3"),
        )

        val genreResultDto = GenreResultDto(
            genreDtos = genreDtos
        )
    }
}