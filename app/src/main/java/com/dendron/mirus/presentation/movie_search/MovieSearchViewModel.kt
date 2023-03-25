package com.dendron.mirus.presentation.movie_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.GetFavoritesMovieUseCase
import com.dendron.mirus.domain.use_case.SearchMoviesUseCase
import com.dendron.mirus.presentation.movie_list.MovieUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getFavoritesMovieUseCase: GetFavoritesMovieUseCase,
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Int>>(emptyList())

    private val _movies = MutableStateFlow(MovieSearchState())
    val movies = combine(_movies, _favorites) { movies, favorites ->
        MovieSearchState(
            movies =
            movies.movies.map { model ->
                val movie = model.movie
                val isFav = favorites.any { it == movie.id }
                MovieUiModel(movie, isFav)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieSearchState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    init {
        getFavoriteMovies()
    }

    private fun getFavoriteMovies() {
        getFavoritesMovieUseCase().onEach { result ->
            _favorites.value = result.map { it.id }
        }.launchIn(viewModelScope)
    }

    fun searchMovie(query: String) {
        searchMoviesUseCase(query = query).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val movies = result.data.map { movie ->
                        MovieUiModel(movie, false)
                    }
                    _movies.value =
                        MovieSearchState(movies = movies)
                }
                is Resource.Error -> _movies.value =
                    MovieSearchState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _movies.value = MovieSearchState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}