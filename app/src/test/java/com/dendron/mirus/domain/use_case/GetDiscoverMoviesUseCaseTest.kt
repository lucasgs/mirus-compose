package com.dendron.mirus.domain.use_case

import app.cash.turbine.test
import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.movies
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class GetDiscoverMoviesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    private lateinit var getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase

    @Before
    fun setUp() {
        getDiscoverMoviesUseCase = GetDiscoverMoviesUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository to get discovery movies`() = runTest {
        getDiscoverMoviesUseCase.invoke().collect()
        verify(movieRepository).getDiscoverMovies()
    }

    @Test
    fun `invoke should return loading and success when the repository returns data`() = runTest {
        val expectedLoading = Resource.Loading<Movie>()
        val expectedSuccess = Resource.Success(data = movies.first())
        whenever(movieRepository.getDiscoverMovies()).thenReturn(
            flowOf(movies)
        )

        getDiscoverMoviesUseCase().test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedSuccess, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns httpexception`() = runTest {
        val expectedErrorMessage = "error"
        val expectedLoading = Resource.Loading<Movie>()
        val expectedError = Resource.Error<Movie>(message = expectedErrorMessage)
        whenever(movieRepository.getDiscoverMovies()).thenThrow(HttpException::class.java)

        getDiscoverMoviesUseCase().test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedError, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns ioexception`() = runTest {
        val expectedErrorMessage = "error"
        val expectedLoading = Resource.Loading<Movie>()
        val expectedError = Resource.Error<Movie>(message = expectedErrorMessage)
        whenever(movieRepository.getDiscoverMovies()).thenAnswer {
            throw IOException()
        }

        getDiscoverMoviesUseCase().test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedError, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns exception`() = runTest {
        val expectedErrorMessage = "error"
        val expectedLoading = Resource.Loading<Movie>()
        val expectedError = Resource.Error<Movie>(message = expectedErrorMessage)
        whenever(movieRepository.getDiscoverMovies()).thenAnswer {
            throw Exception(expectedErrorMessage)
        }

        getDiscoverMoviesUseCase().test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedError, awaitItem())
            awaitComplete()
        }
    }
}