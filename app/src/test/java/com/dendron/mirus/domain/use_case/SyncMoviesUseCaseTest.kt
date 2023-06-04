package com.dendron.mirus.domain.use_case

import app.cash.turbine.test
import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.NetworkChecker
import com.dendron.mirus.domain.repository.GenreRepository
import com.dendron.mirus.domain.repository.MovieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SyncMoviesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var genreRepository: GenreRepository

    @Mock
    private lateinit var networkChecker: NetworkChecker

    private lateinit var syncMoviesUseCase: SyncMoviesUseCase

    @Before
    fun setUp() {
        syncMoviesUseCase = SyncMoviesUseCase(networkChecker, movieRepository, genreRepository)
    }

    @Test
    fun `invoke should call sync movies and genres when the device is online`() = runTest {
        val expected = Resource.Success(true)
        whenever(networkChecker.isOnline()).thenReturn(true)

        syncMoviesUseCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
            verify(movieRepository).syncMovies()
            verify(genreRepository).syncMovieGenres()
        }
    }

    @Test
    fun `invoke should NOT call sync movies and genres when the device is offline`() = runTest {
        whenever(networkChecker.isOnline()).thenReturn(false)

        syncMoviesUseCase().test {
            awaitComplete()
            verify(movieRepository, never()).syncMovies()
            verify(genreRepository, never()).syncMovieGenres()
        }
    }

    @Test
    fun `invoke should emit an error when there's an error in the movie repository`() = runTest {
        val expected = Resource.Error<Boolean>("")

        whenever(networkChecker.isOnline()).thenReturn(true)
        whenever(movieRepository.syncMovies()).thenAnswer {
            throw Exception("")
        }

        syncMoviesUseCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
            verify(movieRepository).syncMovies()
            verify(genreRepository, never()).syncMovieGenres()
        }
    }

    @Test
    fun `invoke should emit an error when there's an error in the genre repository`() = runTest {
        val expected = Resource.Error<Boolean>("")

        whenever(networkChecker.isOnline()).thenReturn(true)
        whenever(genreRepository.syncMovieGenres()).thenAnswer {
            throw Exception("")
        }

        syncMoviesUseCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
            verify(movieRepository).syncMovies()
            verify(genreRepository).syncMovieGenres()
        }
    }
}