package com.dendron.mirus.domain.use_case

import app.cash.turbine.test
import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
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
class ToggleMovieFavoriteUseCaseTestEntity {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var favoriteMovieRepository: FavoriteMovieRepository

    private lateinit var toogleMovieFavoriteUseCase: ToggleMovieFavoriteUseCase

    @Before
    fun setUp() {
        toogleMovieFavoriteUseCase = ToggleMovieFavoriteUseCase(favoriteMovieRepository)
    }

    @Test
    fun `invoke should call repository to save discovery movies`() = runTest {
        toogleMovieFavoriteUseCase.invoke(movie, false).collect()
        verify(favoriteMovieRepository).saveFavoriteMovie(movie)
    }

    @Test
    fun `invoke should call repository to remove discovery movies`() = runTest {
        toogleMovieFavoriteUseCase.invoke(movie, true).collect()
        verify(favoriteMovieRepository).removeFavoriteMovie(movie)
    }

    @Test
    fun `invoke should return error when the repository returns exception`() = runTest {
        toogleMovieFavoriteUseCase(movie, false).test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    companion object {
        private val movie = movies.first()
    }
}