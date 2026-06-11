package com.dendron.mirus.presentation.movie_detail

import androidx.lifecycle.SavedStateHandle
import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.common.Constants
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import com.dendron.mirus.domain.repository.GenreRepository
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.domain.use_case.GetFavoritesMovieUseCase
import com.dendron.mirus.domain.use_case.GetMovieDetailsUseCase
import com.dendron.mirus.domain.use_case.GetMovieGenresUseCase
import com.dendron.mirus.domain.use_case.ToggleMovieFavoriteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var movieRepository: MovieRepository

    @Mock
    private lateinit var favoriteMovieRepository: FavoriteMovieRepository

    @Mock
    private lateinit var genreRepository: GenreRepository

    @Test
    fun `init should combine detail favorites and genres into a single state`() = runTest {
        val favoriteMoviesFlow = MutableStateFlow<List<Movie>>(emptyList())
        val genresFlow = MutableStateFlow<List<Genre>>(emptyList())
        whenever(movieRepository.getMovieDetails(MOVIE_ID)).thenReturn(flowOf(movie))
        whenever(favoriteMovieRepository.getFavoriteMovies()).thenReturn(favoriteMoviesFlow)
        whenever(genreRepository.getGenres()).thenReturn(genresFlow)

        val viewModel = createViewModel()
        favoriteMoviesFlow.value = listOf(movie)
        genresFlow.value = listOf(Genre(1, "Action"))
        advanceUntilIdle()

        with(viewModel.state.value) {
            assertFalse(isLoading)
            assertEquals(movie, model?.movie)
            assertTrue(model?.isFavorite == true)
            assertEquals(listOf(Genre(1, "Action")), model?.genres)
            assertTrue(error.isEmpty())
        }
    }

    @Test
    fun `toggleMovieAsFavorite should update favorite state optimistically`() = runTest {
        val favoriteMoviesFlow = MutableStateFlow<List<Movie>>(emptyList())
        val genresFlow = MutableStateFlow(listOf(Genre(1, "Action")))
        whenever(movieRepository.getMovieDetails(MOVIE_ID)).thenReturn(flowOf(movie))
        whenever(favoriteMovieRepository.getFavoriteMovies()).thenReturn(favoriteMoviesFlow)
        whenever(genreRepository.getGenres()).thenReturn(genresFlow)

        val viewModel = createViewModel()
        val initialModel = requireNotNull(viewModel.state.value.model)
        assertFalse(initialModel.isFavorite)

        viewModel.toggleMovieAsFavorite(initialModel)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.model?.isFavorite == true)
        verify(favoriteMovieRepository).saveFavoriteMovie(movie)
    }

    @Test
    fun `toggleMovieAsFavorite should not start new favorite collectors`() = runTest {
        val favoriteMoviesFlow = MutableStateFlow<List<Movie>>(emptyList())
        val genresFlow = MutableStateFlow<List<Genre>>(emptyList())
        whenever(movieRepository.getMovieDetails(MOVIE_ID)).thenReturn(flowOf(movie))
        whenever(favoriteMovieRepository.getFavoriteMovies()).thenReturn(favoriteMoviesFlow)
        whenever(genreRepository.getGenres()).thenReturn(genresFlow)

        val viewModel = createViewModel()
        val initialModel = requireNotNull(viewModel.state.value.model)

        viewModel.toggleMovieAsFavorite(initialModel)
        advanceUntilIdle()

        verify(favoriteMovieRepository, times(1)).getFavoriteMovies()
    }

    private fun createViewModel(): MovieDetailViewModel = MovieDetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf(Constants.MOVIE_ID_KEY to MOVIE_ID)),
        getMovieDetailsUseCase = GetMovieDetailsUseCase(movieRepository),
        toggleMovieFavoriteUseCase = ToggleMovieFavoriteUseCase(favoriteMovieRepository),
        getFavoriteMovieUseCase = GetFavoritesMovieUseCase(favoriteMovieRepository),
        getMovieGenresUseCase = GetMovieGenresUseCase(genreRepository)
    )

    private companion object {
        const val MOVIE_ID = "1"

        val movie = Movie(
            id = 1,
            overview = "overview",
            popularity = 1.0,
            posterPath = "poster",
            releaseDate = "2023-01-01",
            title = "title",
            backDropPath = "backdrop",
            voteAverage = 8.0,
            genres = listOf(1)
        )
    }
}
