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
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val toggleMovieFavoriteUseCase: ToggleMovieFavoriteUseCase,
    private val getFavoriteMovieUseCase: GetFavoritesMovieUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
) : ViewModel() {
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _genres = MutableStateFlow<Map<Int, Genre>>(emptyMap())
    private val _state = MutableStateFlow(MovieDetailState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        observeFavoriteMovies()
        observeGenres()
        observeMovieDetail()
    }

    fun toggleMovieAsFavorite(model: MovieUiModel) {
        val updatedFavoriteState = !model.isFavorite
        updateMovieModel { currentModel ->
            currentModel.copy(isFavorite = updatedFavoriteState)
        }

        toggleMovieFavoriteUseCase(
            movie = model.movie,
            isFavorite = model.isFavorite
        ).onEach { result ->
            if (result is Resource.Error) {
                updateMovieModel { currentModel ->
                    currentModel.copy(isFavorite = model.isFavorite)
                }
                _state.update { currentState ->
                    currentState.copy(error = result.message ?: "An expected error occurred")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun observeFavoriteMovies() {
        getFavoriteMovieUseCase().onEach { result ->
            _favoriteIds.value = result.map { movie -> movie.id }.toSet()
            syncDerivedState()
        }.launchIn(viewModelScope)
    }

    private fun observeGenres() {
        getMovieGenresUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _genres.value = result.data.orEmpty().associateBy { genre -> genre.id }
                    syncDerivedState()
                }

                is Resource.Error -> {
                    _state.update { currentState ->
                        currentState.copy(error = result.message ?: "An expected error occurred")
                    }
                }

                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun observeMovieDetail() {
        savedStateHandle.get<String>(Constants.MOVIE_ID_KEY)?.let { movieId ->
            getMovieDetailsUseCase(movieId).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val movie = result.data
                        if (movie != null) {
                            _state.value = MovieDetailState(
                                isLoading = false,
                                model = MovieUiModel(
                                    movie = movie,
                                    isFavorite = _favoriteIds.value.contains(movie.id),
                                    genres = movie.genres.mapGenreModels()
                                ),
                                error = ""
                            )
                        } else {
                            _state.value = MovieDetailState(
                                isLoading = false,
                                error = "Error loading details"
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.value = MovieDetailState(
                            isLoading = false,
                            error = result.message ?: "An expected error occurred"
                        )
                    }

                    is Resource.Loading -> {
                        _state.update { currentState ->
                            currentState.copy(isLoading = true, error = "")
                        }
                    }
                }
            }.launchIn(viewModelScope)
        } ?: run {
            _state.value = MovieDetailState(isLoading = false, error = "Error loading details")
        }
    }

    private fun syncDerivedState() {
        updateMovieModel { currentModel ->
            currentModel.copy(
                isFavorite = _favoriteIds.value.contains(currentModel.movie.id),
                genres = currentModel.movie.genres.mapGenreModels()
            )
        }
    }

    private fun updateMovieModel(transform: (MovieUiModel) -> MovieUiModel) {
        _state.update { currentState ->
            val currentModel = currentState.model ?: return@update currentState
            currentState.copy(model = transform(currentModel))
        }
    }

    private fun List<Int>.mapGenreModels(): List<Genre> =
        map { genreId -> _genres.value[genreId] ?: Genre(0, "") }
}
