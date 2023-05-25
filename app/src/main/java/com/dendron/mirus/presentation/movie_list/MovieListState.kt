package com.dendron.mirus.presentation.movie_list

import com.dendron.mirus.domain.model.Movie

data class MovieListState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String = ""
)