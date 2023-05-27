package com.dendron.mirus.domain.use_case

import app.cash.turbine.test
import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
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
class GetFavoritesMoviesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var favoriteMovieRepository: FavoriteMovieRepository

    private lateinit var getFavoritesMovieUseCase: GetFavoritesMovieUseCase

    @Before
    fun setUp() {
        getFavoritesMovieUseCase = GetFavoritesMovieUseCase(favoriteMovieRepository)
    }

    @Test
    fun `invoke should call repository to get discovery movies`() = runTest {
        whenever(favoriteMovieRepository.getFavoriteMovies()).thenReturn(
            flowOf(movies)
        )
        getFavoritesMovieUseCase.invoke().collect()
        verify(favoriteMovieRepository).getFavoriteMovies()
    }

    @Test
    fun `invoke should return loading and success when the repository returns data`() = runTest {
        val expected = movies

        whenever(favoriteMovieRepository.getFavoriteMovies()).thenReturn(
            flowOf(movies)
        )

        getFavoritesMovieUseCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns httpexception`() = runTest {
        whenever(favoriteMovieRepository.getFavoriteMovies()).thenThrow(HttpException::class.java)

        getFavoritesMovieUseCase().test {
            awaitError()
        }
    }

    @Test
    fun `invoke should return error when the repository returns ioexception`() = runTest {
        whenever(favoriteMovieRepository.getFavoriteMovies()).thenAnswer {
            throw IOException()
        }

        getFavoritesMovieUseCase().test {
            awaitError()
        }
    }

    @Test
    fun `invoke should return error when the repository returns exception`() = runTest {
        val expectedErrorMessage = "error"
        whenever(favoriteMovieRepository.getFavoriteMovies()).thenAnswer {
            throw Exception(expectedErrorMessage)
        }

        getFavoritesMovieUseCase().test {
            awaitError()
        }
    }

}