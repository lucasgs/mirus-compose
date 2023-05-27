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
class SearchMoviesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    private lateinit var searchMoviesUseCase: SearchMoviesUseCase

    @Before
    fun setUp() {
        searchMoviesUseCase = SearchMoviesUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository to get discovery movies`() = runTest {
        searchMoviesUseCase.invoke(QUERY).collect()
        verify(movieRepository).searchMovies(QUERY)
    }

    @Test
    fun `invoke should return loading and success when the repository returns data`() = runTest {
        val expectedLoading = Resource.Loading<List<Movie>>()
        val expectedSuccess = Resource.Success(data = movies)
        whenever(movieRepository.searchMovies(QUERY)).thenReturn(
            flowOf(movies)
        )

        searchMoviesUseCase(QUERY).test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedSuccess, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns httpexception`() = runTest {
        val expectedErrorMessage = "error"
        val expectedLoading = Resource.Loading<List<Movie>>()
        val expectedError = Resource.Error<List<Movie>>(message = expectedErrorMessage)
        whenever(movieRepository.searchMovies(QUERY)).thenThrow(HttpException::class.java)

        searchMoviesUseCase(QUERY).test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedError, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns ioexception`() = runTest {
        val expectedErrorMessage = "error"
        val expectedLoading = Resource.Loading<List<Movie>>()
        val expectedError = Resource.Error<List<Movie>>(message = expectedErrorMessage)
        whenever(movieRepository.searchMovies(QUERY)).thenAnswer {
            throw IOException()
        }

        searchMoviesUseCase(QUERY).test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedError, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns exception`() = runTest {
        val expectedErrorMessage = "error"
        val expectedLoading = Resource.Loading<List<Movie>>()
        val expectedError = Resource.Error<List<Movie>>(message = expectedErrorMessage)
        whenever(movieRepository.searchMovies(QUERY)).thenAnswer {
            throw Exception(expectedErrorMessage)
        }

        searchMoviesUseCase(QUERY).test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedError, awaitItem())
            awaitComplete()
        }
    }

    companion object {
        private const val QUERY = "query"
    }
}