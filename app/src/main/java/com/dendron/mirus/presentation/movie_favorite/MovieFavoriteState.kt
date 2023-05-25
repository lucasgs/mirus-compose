package com.dendron.mirus.presentation.movie_favorite

import com.dendron.mirus.domain.model.Movie

class MovieFavoriteState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String = ""
)