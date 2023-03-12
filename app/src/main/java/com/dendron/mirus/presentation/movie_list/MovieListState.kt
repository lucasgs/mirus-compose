package com.dendron.mirus.presentation.movie_list

class MovieListState(
    val isLoading: Boolean = false,
    val movies: List<MovieUiModel> = emptyList(),
    val error: String = ""
)