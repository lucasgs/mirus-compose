package com.dendron.mirus.presentation.movie_search

import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.domain.use_case.SearchMoviesUseCase
import com.dendron.mirus.movies
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MovieSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    private lateinit var viewModel: MovieSearchViewModel

    @Before
    fun setUp() {
        viewModel = MovieSearchViewModel(SearchMoviesUseCase(movieRepository))
    }

    @Test
    fun `onQueryChanged should debounce search and update state`() = runTest {
        whenever(movieRepository.searchMovies(QUERY)).thenReturn(flowOf(movies))

        viewModel.onQueryChanged(QUERY)
        advanceTimeBy(300)
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertEquals(QUERY, query)
            assertEquals(movies, this.movies)
            assertFalse(isLoading)
            assertTrue(error.isEmpty())
        }
        verify(movieRepository).searchMovies(QUERY)
    }

    @Test
    fun `onQueryChanged should clear results for blank query`() = runTest {
        whenever(movieRepository.searchMovies(QUERY)).thenReturn(flowOf(movies))

        viewModel.onQueryChanged(QUERY)
        advanceTimeBy(300)
        advanceUntilIdle()

        viewModel.onQueryChanged("   ")
        advanceTimeBy(300)
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertTrue(query.isBlank())
            assertTrue(movies.isEmpty())
            assertFalse(isLoading)
            assertTrue(error.isEmpty())
        }
    }

    @Test
    fun `onSearchSubmitted should keep unique recent searches in most recent order`() {
        viewModel.onQueryChanged(" first ")
        viewModel.onSearchSubmitted()

        viewModel.onQueryChanged("second")
        viewModel.onSearchSubmitted()

        viewModel.onQueryChanged("FIRST")
        viewModel.onSearchSubmitted()

        assertEquals(listOf("FIRST", "second"), viewModel.state.value.recentSearches)
    }

    private companion object {
        const val QUERY = "query"
    }
}
