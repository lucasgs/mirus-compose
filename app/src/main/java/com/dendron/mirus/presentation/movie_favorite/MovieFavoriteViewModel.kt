package com.dendron.mirus.presentation.movie_favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.domain.use_case.GetFavoritesMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MovieFavoriteViewModel @Inject constructor(
    getFavoriteMovieUseCase: GetFavoritesMovieUseCase,
) : ViewModel() {

    val movies = getFavoriteMovieUseCase().map { result ->
        MovieFavoriteState(
            movies = result
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieFavoriteState(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )
}
