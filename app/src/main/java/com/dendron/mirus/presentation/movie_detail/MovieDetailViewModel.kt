package com.dendron.mirus.presentation.movie_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Constants
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.use_case.GetFavoritesMovieUseCase
import com.dendron.mirus.domain.use_case.GetMovieDetailsUseCase
import com.dendron.mirus.domain.use_case.GetMovieGenresUseCase
import com.dendron.mirus.domain.use_case.ToggleMovieFavoriteUseCase
import com.dendron.mirus.presentation.movie_list.MovieUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val toggleMovieFavoriteUseCase: ToggleMovieFavoriteUseCase,
    private val getFavoriteMovieUseCase: GetFavoritesMovieUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
) : ViewModel() {
    private val _favorites = MutableStateFlow<List<Int>>(emptyList())
    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    private val _state = MutableStateFlow(MovieDetailState())

    val state = combine(_state, _favorites, _genres) { movieDetail, favorites, genres ->
        movieDetail.model?.movie?.let { movie ->
            val isFav = favorites.any { it == movie.id }
            MovieDetailState(
                model = MovieUiModel(
                    movie,
                    isFav,
                    movie.genres.map { genre -> genres.find { genre == it.id } ?: Genre(0, "") }
                ))

        } ?: MovieDetailState(error = "Error loading details")
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieDetailState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    init {
        getFavoriteMovies()
        getMovieDetail()
        getGenres()
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
        viewModelScope.launch {
            getFavoriteMovieUseCase().onEach { result ->
                _favorites.value = result.map { it.id }
            }.launchIn(viewModelScope)
        }
    }

    private fun getGenres() {
        viewModelScope.launch {
            getMovieGenresUseCase().onEach { result ->
                val genres = when (result) {
                    is Resource.Error, is Resource.Loading -> emptyList<Genre>()
                    is Resource.Success -> result.data
                }
                _genres.update {
                    genres
                }
            }.launchIn(viewModelScope)
        }
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
                        val model =
                            MovieUiModel(movie = movie, isFavorite = false, genres = emptyList())
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