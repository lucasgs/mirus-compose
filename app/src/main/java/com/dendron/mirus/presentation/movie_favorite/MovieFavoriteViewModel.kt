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
            emit(MovieFavoriteState(movies = movieList.map { movie ->
                MovieUiModel(movie = movie, true)
            }))
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieFavoriteState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    val movies2: StateFlow<MovieFavoriteState> = _favorites.map { favorites ->
        val list = flow {
            favorites.map { favorite ->
                getMovieDetailsUseCase(favorite.toString()).onEach { result ->
                    when (result) {
                        is Resource.Success -> emit(
                            MovieUiModel(
                                movie = result.data, isFavorite = true
                            )
                        )
                        else -> {}
                    }
                }.collect()
            }
        }.toList()
        MovieFavoriteState(
            movies = list
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieFavoriteState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    init {
        //refresh()
    }

    fun refresh() {
        getFavoriteMovies()
    }

    fun toggleMovieAsFavorite(model: MovieUiModel) {
        toggleMovieFavoriteUseCase(
            movie = model.movie, isFavorite = model.isFavorite
        ).launchIn(viewModelScope)
    }

    private fun getFavoriteMovies() {
        getFavoriteMovieUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> _favorites.value = result.data
                is Resource.Error -> _favorites.value = emptyList()
                is Resource.Loading -> _favorites.value = emptyList()
            }
        }.launchIn(viewModelScope)
    }
}
