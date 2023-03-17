package com.dendron.mirus.presentation.movie_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Constants
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.GetFavoritesMovieUseCase
import com.dendron.mirus.domain.use_case.GetMovieDetailsUseCase
import com.dendron.mirus.domain.use_case.ToggleMovieFavoriteUseCase
import com.dendron.mirus.presentation.movie_list.MovieUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val toggleMovieFavoriteUseCase: ToggleMovieFavoriteUseCase,
    private val getFavoriteMovieUseCase: GetFavoritesMovieUseCase,
) : ViewModel() {
    private val _favorites = MutableStateFlow<List<Int>>(emptyList())

    private val _state = MutableStateFlow(MovieDetailState())

    val state = combine(_state, _favorites) { movieDetail, favorites ->
        movieDetail.model?.movie?.let { movie ->
            val isFav = favorites.any { it == movie.id }
            MovieDetailState(
                model = MovieUiModel(movie, isFav)
            )
        } ?: MovieDetailState(error = "Error loading details")
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieDetailState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    init {
        getFavoriteMovies()
        getMovieDetail()
    }

    fun toggleMovieAsFavorite(model: MovieUiModel) {
        toggleMovieFavoriteUseCase(
            movie = model.movie,
            isFavorite = model.isFavorite
        ).onEach {
            getFavoriteMovies()
        }.launchIn(viewModelScope)
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

    private fun getMovieDetail() {
        savedStateHandle.get<String>(Constants.MOVIE_ID_KEY)?.let { movieId ->
            getMovies(movieId = movieId)
        }
    }

    private fun getMovies(movieId: String) {
        getMovieDetailsUseCase(movieId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data.let { movie ->
                        val model = MovieUiModel(movie = movie, false)
                        _state.value = MovieDetailState(model = model)
                    }
                }
                is Resource.Error -> _state.value =
                    MovieDetailState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _state.value = MovieDetailState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}