package com.dendron.mirus.domain.use_case

import app.cash.turbine.test
import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.NetworkChecker
import com.dendron.mirus.domain.repository.GenreRepository
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.domain.repository.SyncMetadataRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
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

    @Mock
    private lateinit var syncMetadataRepository: SyncMetadataRepository

    private lateinit var syncMoviesUseCase: SyncMoviesUseCase

    @Before
    fun setUp() {
        syncMoviesUseCase = SyncMoviesUseCase(
            networkChecker = networkChecker,
            movieRepository = movieRepository,
            genreRepository = genreRepository,
            syncMetadataRepository = syncMetadataRepository,
        )
    }

    @Test
    fun `invoke should call sync movies and genres when the device is online`() = runTest {
        whenever(networkChecker.isOnline()).thenReturn(true)

        syncMoviesUseCase().test {
            assertEquals(Resource.Loading<Boolean>(), awaitItem())
            assertEquals(Resource.Success(true), awaitItem())
            awaitComplete()
            verify(movieRepository).syncMovies()
            verify(genreRepository).syncMovieGenres()
            verify(syncMetadataRepository).recordSuccessfulSync(any())
        }
    }

    @Test
    fun `invoke should NOT call sync movies and genres when the device is offline`() = runTest {
        whenever(networkChecker.isOnline()).thenReturn(false)

        syncMoviesUseCase().test {
            assertEquals(Resource.Loading<Boolean>(), awaitItem())
            assertEquals(Resource.Error<Boolean>(SyncMoviesUseCase.OFFLINE_MESSAGE), awaitItem())
            awaitComplete()
            verify(movieRepository, never()).syncMovies()
            verify(genreRepository, never()).syncMovieGenres()
        }
    }

    @Test
    fun `invoke should emit an error when there's an error in the movie repository`() = runTest {
        whenever(networkChecker.isOnline()).thenReturn(true)
        whenever(movieRepository.syncMovies()).thenAnswer {
            throw Exception("")
        }

        syncMoviesUseCase().test {
            assertEquals(Resource.Loading<Boolean>(), awaitItem())
            assertEquals(Resource.Error<Boolean>(""), awaitItem())
            awaitComplete()
            verify(movieRepository).syncMovies()
            verify(genreRepository, never()).syncMovieGenres()
        }
    }

    @Test
    fun `invoke should emit an error when there's an error in the genre repository`() = runTest {
        whenever(networkChecker.isOnline()).thenReturn(true)
        whenever(genreRepository.syncMovieGenres()).thenAnswer {
            throw Exception("")
        }

        syncMoviesUseCase().test {
            assertEquals(Resource.Loading<Boolean>(), awaitItem())
            assertEquals(Resource.Error<Boolean>(""), awaitItem())
            awaitComplete()
            verify(movieRepository).syncMovies()
            verify(genreRepository).syncMovieGenres()
        }
    }
}
