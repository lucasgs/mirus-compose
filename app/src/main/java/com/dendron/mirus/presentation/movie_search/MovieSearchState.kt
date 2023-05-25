package com.dendron.mirus.presentation.movie_search

import com.dendron.mirus.domain.model.Movie

data class MovieSearchState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val query: String = "",
    val error: String = ""
)