package com.dendron.mirus.presentation.movie_favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.domain.use_case.GetFavoritesMovieUseCase
import com.dendron.mirus.domain.use_case.ToggleMovieFavoriteUseCase
import com.dendron.mirus.presentation.movie_list.MovieUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MovieFavoriteViewModel @Inject constructor(
    getFavoriteMovieUseCase: GetFavoritesMovieUseCase,
    private val toggleMovieFavoriteUseCase: ToggleMovieFavoriteUseCase,
) : ViewModel() {

    val movies = getFavoriteMovieUseCase().map { result ->
        MovieFavoriteState(
            movies = result.map {
                MovieUiModel(
                    it, true
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieFavoriteState(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    fun toggleMovieAsFavorite(model: MovieUiModel) {
        toggleMovieFavoriteUseCase(
            movie = model.movie, isFavorite = model.isFavorite
        ).launchIn(viewModelScope)
    }
}
