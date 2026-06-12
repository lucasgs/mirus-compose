package com.dendron.mirus.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dendron.mirus.common.Constants
import com.dendron.mirus.domain.NetworkChecker
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.FavoriteMovieRepository
import com.dendron.mirus.domain.repository.GenreRepository
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.domain.use_case.GetDiscoverMoviesUseCase
import com.dendron.mirus.domain.use_case.GetFavoritesMovieUseCase
import com.dendron.mirus.domain.use_case.GetMovieDetailsUseCase
import com.dendron.mirus.domain.use_case.GetMovieGenresUseCase
import com.dendron.mirus.domain.use_case.GetTopRatedMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTrendingMoviesUseCase
import com.dendron.mirus.domain.use_case.SearchMoviesUseCase
import com.dendron.mirus.domain.use_case.SyncMoviesUseCase
import com.dendron.mirus.domain.use_case.ToggleMovieFavoriteUseCase
import com.dendron.mirus.presentation.movie_detail.MovieDetailViewModel
import com.dendron.mirus.presentation.movie_favorite.MovieFavoriteViewModel
import com.dendron.mirus.presentation.movie_list.MovieListViewModel
import com.dendron.mirus.presentation.movie_search.MovieSearchViewModel
import com.dendron.mirus.ui.movie_detail.MovieDetailScreen
import com.dendron.mirus.ui.movie_favorite.MovieFavoriteScreen
import com.dendron.mirus.ui.movie_list.MovieListWrapper
import com.dendron.mirus.ui.movie_search.MovieSearchScreen
import com.dendron.mirus.ui.theme.MirusTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieScreensTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun movieListWrapper_shows_all_home_sections() {
        val movieRepository = FakeMovieRepository(
            discoverMovies = listOf(sampleMovie(id = 1, title = "Discover")),
            topRatedMovies = listOf(sampleMovie(id = 2, title = "Top Rated")),
            trendingMovies = listOf(sampleMovie(id = 3, title = "Trending"))
        )
        val viewModel = MovieListViewModel(
            getDiscoverMoviesUseCase = GetDiscoverMoviesUseCase(movieRepository),
            getTopRatedMoviesUseCase = GetTopRatedMoviesUseCase(movieRepository),
            getTrendingMoviesUseCase = GetTrendingMoviesUseCase(movieRepository),
            syncMoviesUseCase = SyncMoviesUseCase(
                networkChecker = FakeNetworkChecker(isOnline = false),
                movieRepository = movieRepository,
                genreRepository = FakeGenreRepository()
            )
        )

        composeRule.setContent {
            MirusTheme {
                MovieListWrapper(
                    navController = rememberNavController(),
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithText("Top rated").fetchSemanticsNode()
        composeRule.onNodeWithText("Discovery").fetchSemanticsNode()
        composeRule.onNodeWithText("Trending").fetchSemanticsNode()
    }

    @Test
    fun movieSearchScreen_shows_recent_searches() {
        val viewModel = MovieSearchViewModel(SearchMoviesUseCase(FakeMovieRepository()))
        viewModel.onQueryChanged("Batman")
        viewModel.onSearchSubmitted()
        viewModel.onQueryChanged("")

        composeRule.setContent {
            MirusTheme {
                MovieSearchScreen(
                    navController = rememberNavController(),
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithText("Recent searches").fetchSemanticsNode()
        composeRule.onNodeWithText("Batman").fetchSemanticsNode()
    }

    @Test
    fun movieFavoriteScreen_shows_empty_state_when_no_favorites_exist() {
        val viewModel = MovieFavoriteViewModel(
            getFavoriteMovieUseCase = GetFavoritesMovieUseCase(FakeFavoriteMovieRepository())
        )

        composeRule.setContent {
            MirusTheme {
                MovieFavoriteScreen(
                    navController = rememberNavController(),
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithText("No favorites movies yet").fetchSemanticsNode()
    }

    @Test
    fun movieDetailScreen_shows_movie_title_and_genres() {
        val movie = sampleMovie(id = 42, title = "The Detail")
        val favoriteRepository = FakeFavoriteMovieRepository(
            favorites = listOf(movie)
        )
        val viewModel = MovieDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(Constants.MOVIE_ID_KEY to movie.id.toString())),
            getMovieDetailsUseCase = GetMovieDetailsUseCase(
                FakeMovieRepository(movieDetails = movie)
            ),
            toggleMovieFavoriteUseCase = ToggleMovieFavoriteUseCase(favoriteRepository),
            getFavoriteMovieUseCase = GetFavoritesMovieUseCase(favoriteRepository),
            getMovieGenresUseCase = GetMovieGenresUseCase(
                FakeGenreRepository(
                    genres = listOf(
                        Genre(id = 1, name = "Action"),
                        Genre(id = 2, name = "Drama")
                    )
                )
            )
        )

        composeRule.setContent {
            MirusTheme {
                MovieDetailScreen(
                    viewModel = viewModel,
                    navController = rememberNavController()
                )
            }
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithText("The Detail").fetchSemanticsNode()
            }.isSuccess
        }

        composeRule.onNodeWithText("The Detail").fetchSemanticsNode()
        composeRule.onNodeWithText("Action").fetchSemanticsNode()
        composeRule.onNodeWithText("Drama").fetchSemanticsNode()
    }

    companion object {
        private fun sampleMovie(id: Int, title: String) = Movie(
            id = id,
            overview = "Overview for $title",
            popularity = 10.0,
            posterPath = "/poster/$id",
            releaseDate = "2024-01-01",
            title = title,
            backDropPath = "/backdrop/$id",
            voteAverage = 7.8,
            genres = listOf(1, 2)
        )
    }

    private class FakeMovieRepository(
        private val discoverMovies: List<Movie> = emptyList(),
        private val topRatedMovies: List<Movie> = emptyList(),
        private val trendingMovies: List<Movie> = emptyList(),
        private val movieDetails: Movie = sampleMovie(id = 99, title = "Fallback"),
        private val searchResults: Map<String, List<Movie>> = emptyMap()
    ) : MovieRepository {
        override fun getDiscoverMovies(): Flow<List<Movie>> = flowOf(discoverMovies)

        override fun getTopRatedMovies(): Flow<List<Movie>> = flowOf(topRatedMovies)

        override fun getTrendingMovies(): Flow<List<Movie>> = flowOf(trendingMovies)

        override fun getMovieDetails(movieId: String): Flow<Movie> = flowOf(movieDetails)

        override fun searchMovies(query: String): Flow<List<Movie>> =
            flowOf(searchResults[query].orEmpty())

        override suspend fun syncMovies() = Unit
    }

    private class FakeFavoriteMovieRepository(
        favorites: List<Movie> = emptyList()
    ) : FavoriteMovieRepository {
        private val favoriteMovies = MutableStateFlow(favorites)

        override suspend fun saveFavoriteMovie(movie: Movie) {
            favoriteMovies.value = favoriteMovies.value + movie
        }

        override suspend fun removeFavoriteMovie(movie: Movie) {
            favoriteMovies.value = favoriteMovies.value.filterNot { it.id == movie.id }
        }

        override suspend fun isFavoriteMovie(movie: Movie): Boolean =
            favoriteMovies.value.any { it.id == movie.id }

        override fun getFavoriteMovies(): Flow<List<Movie>> = favoriteMovies
    }

    private class FakeGenreRepository(
        private val genres: List<Genre> = emptyList()
    ) : GenreRepository {
        override fun getGenreDetails(genresId: List<Int>): Flow<List<Genre>> =
            flowOf(genres.filter { it.id in genresId })

        override fun getGenres(): Flow<List<Genre>> = flowOf(genres)

        override suspend fun syncMovieGenres() = Unit
    }

    private class FakeNetworkChecker(
        private val isOnline: Boolean
    ) : NetworkChecker {
        override fun isOnline(): Boolean = isOnline
    }
}
