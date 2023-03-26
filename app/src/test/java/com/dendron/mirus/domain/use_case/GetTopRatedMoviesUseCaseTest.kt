package com.dendron.mirus.domain.use_case

import app.cash.turbine.test
import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.movies
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
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
class GetTopRatedMoviesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    private lateinit var getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase

    @Before
    fun setUp() {
        getTopRatedMoviesUseCase = GetTopRatedMoviesUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository to get discovery movies`() = runTest {
        getTopRatedMoviesUseCase.invoke().collect()
        verify(movieRepository).getTopRatedMovies()
    }

    @Test
    fun `invoke should return loading and success when the repository returns data`() = runTest {
        val expectedLoading = Resource.Loading<Movie>()
        val expectedSuccess = Resource.Success(data = movies.first())
        whenever(movieRepository.getTopRatedMovies()).thenReturn(
            movies
        )

        getTopRatedMoviesUseCase().test {
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
        whenever(movieRepository.getTopRatedMovies()).thenThrow(HttpException::class.java)

        getTopRatedMoviesUseCase().test {
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
        whenever(movieRepository.getTopRatedMovies()).thenAnswer {
            throw IOException()
        }

        getTopRatedMoviesUseCase().test {
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
        whenever(movieRepository.getTopRatedMovies()).thenAnswer {
            throw Exception(expectedErrorMessage)
        }

        getTopRatedMoviesUseCase().test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedError, awaitItem())
            awaitComplete()
        }
    }
}