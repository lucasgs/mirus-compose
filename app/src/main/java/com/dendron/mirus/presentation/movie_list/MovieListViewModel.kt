package com.dendron.mirus.presentation.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val getFavoriteMovieUseCase: GetFavoritesMovieUseCase,
    private val toggleMovieFavoriteUseCase: ToggleMovieFavoriteUseCase,
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Int>>(emptyList())

    private val _discoverMovies = MutableStateFlow(MovieListState())
    val discoverMovies = combine(_discoverMovies, _favorites) { movies, favorites ->
        MovieListState(
            movies =
            movies.movies.map { model ->
                val movie = model.movie
                val isFav = favorites.any { it == movie.id }
                MovieUiModel(movie, isFav)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieListState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    private val _topRatedMovies = MutableStateFlow(MovieListState())
    val topRatedMovies = combine(_topRatedMovies, _favorites) { movies, favorites ->
        MovieListState(
            movies =
            movies.movies.map { model ->
                val movie = model.movie
                val isFav = favorites.any { it == movie.id }
                MovieUiModel(movie, isFav)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieListState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    private val _trendingMovies = MutableStateFlow(MovieListState())
    val trendingMovies = combine(_trendingMovies, _favorites) { movies, favorites ->
        MovieListState(
            movies = movies.movies.map { model ->
                val movie = model.movie
                val isFav = favorites.any { it == movie.id }
                MovieUiModel(movie, isFav)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieListState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    val isLoading =
        combine(
            _discoverMovies,
            _topRatedMovies,
            _trendingMovies
        ) { discover, topRated, trending ->
            discover.isLoading || topRated.isLoading || trending.isLoading
        }.stateIn(
            scope = viewModelScope,
            initialValue = false,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
        )

    val isError =
        combine(
            _discoverMovies,
            _topRatedMovies,
            _trendingMovies
        ) { discover, topRated, trending ->
            buildList {
                if (discover.error.isNotEmpty()) {
                    add(discover.error)
                }
                if (topRated.error.isNotEmpty()) {
                    add(topRated.error)
                }
                if (trending.error.isNotEmpty()) {
                    add(trending.error)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
        )

    init {
        refresh()
        getTopRatedMovies()
        getDiscoverMovies()
        getTrendingMovies()
    }

    fun refresh() {
        getFavoriteMovies()
    }

    fun toggleMovieAsFavorite(model: MovieUiModel) {
        viewModelScope.launch {
            toggleMovieFavoriteUseCase(
                movie = model.movie,
                isFavorite = model.isFavorite
            ).onEach { result ->
                if (result) {
                    getFavoriteMovies()
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getFavoriteMovies() {
        getFavoriteMovieUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> _favorites.value = result.data
                is Resource.Error -> _favorites.value =
                    emptyList()
                is Resource.Loading -> _favorites.value = emptyList()
            }
        }.launchIn(viewModelScope)
    }

    private fun getDiscoverMovies() {
        getDiscoverMoviesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val movies = result.data.map { movie ->
                        MovieUiModel(movie, false)
                    }
                    _discoverMovies.value =
                        MovieListState(movies = movies)
                }
                is Resource.Error -> _discoverMovies.value =
                    MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _discoverMovies.value = MovieListState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    private fun getTopRatedMovies() {
        getTopRatedMoviesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val movies = result.data.map { movie ->
                        MovieUiModel(movie, false)
                    }
                    _topRatedMovies.value =
                        MovieListState(movies = movies)
                }
                is Resource.Error -> _topRatedMovies.value =
                    MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _topRatedMovies.value = MovieListState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    private fun getTrendingMovies() {
        getTrendingMoviesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val movies = result.data.map { movie ->
                        MovieUiModel(movie, false)
                    }
                    _trendingMovies.value =
                        MovieListState(movies = movies)
                }
                is Resource.Error -> _trendingMovies.value =
                    MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _trendingMovies.value = MovieListState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}