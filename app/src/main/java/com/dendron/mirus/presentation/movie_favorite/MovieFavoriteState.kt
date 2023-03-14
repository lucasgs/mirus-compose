package com.dendron.mirus.presentation.movie_favorite

import com.dendron.mirus.presentation.movie_list.MovieUiModel

class MovieFavoriteState(
    val isLoading: Boolean = false,
    val movies: List<MovieUiModel> = emptyList(),
    val error: String = ""
)