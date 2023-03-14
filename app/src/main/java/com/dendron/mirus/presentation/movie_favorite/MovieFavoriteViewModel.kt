package com.dendron.mirus.presentation.movie_favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.use_case.GetFavoritesMovieUseCase
import com.dendron.mirus.domain.use_case.GetMovieDetailsUseCase
import com.dendron.mirus.domain.use_case.ToggleMovieFavoriteUseCase
import com.dendron.mirus.presentation.movie_list.MovieUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MovieFavoriteViewModel @Inject constructor(
    private val getFavoriteMovieUseCase: GetFavoritesMovieUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val toggleMovieFavoriteUseCase: ToggleMovieFavoriteUseCase,
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Int>>(emptyList())

    private val _movies = MutableStateFlow(emptyList<Movie>())
    val movies = _movies.flatMapMerge { movieList ->
        flow {
            emit(
                MovieFavoriteState(
                    movies = movieList.map { movie ->
                        MovieUiModel(movie = movie, true)
                    }
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieFavoriteState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    init {
        refreshData()
    }

    fun refreshData() {
        getFavoriteMovies()
        getMovieDetail()
    }

    private fun getMovieDetail() {
        _favorites.onEach { favoriteList ->
            favoriteList.onEach { movieId ->
                getMovieDetailsUseCase(movieId = movieId.toString()).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _movies.emit(
                                _movies.value.plus(result.data).distinct()
                            )
                        }
                        else -> {}
                    }
                }.launchIn(viewModelScope)
            }
        }.launchIn(viewModelScope)
    }

    fun toggleMovieAsFavorite(model: MovieUiModel) {
        toggleMovieFavoriteUseCase(
            movie = model.movie,
            isFavorite = model.isFavorite
        )
        getFavoriteMovies()
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
}