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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class GetMovieDetailsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    @Before
    fun setUp() {
        getMovieDetailsUseCase = GetMovieDetailsUseCase(movieRepository)
    }

    @Test
    fun `invoke should call repository to get discovery movies`() = runTest {
        getMovieDetailsUseCase.invoke(MOVIE_ID).collect()
        verify(movieRepository).getMovieDetails(MOVIE_ID)
    }

    @Test
    fun `invoke should return loading and success when the repository returns data`() = runTest {
        val expectedLoading = Resource.Loading<Movie>()
        val expectedSuccess = Resource.Success(data = movies.first())

        getMovieDetailsUseCase(MOVIE_ID).test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedSuccess, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns exception`() = runTest {
        val expectedLoading = Resource.Loading<Movie>()
        val expectedSuccess = Resource.Success<Movie?>(null)

        getMovieDetailsUseCase(MOVIE_ID).test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedSuccess, awaitItem())
            awaitComplete()
        }
    }

    companion object {
        private const val MOVIE_ID = "1"
    }
}