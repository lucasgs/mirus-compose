package com.dendron.mirus.presentation.movie_search

import com.dendron.mirus.presentation.movie_list.MovieUiModel

class MovieSearchState(
    val isLoading: Boolean = false,
    val movies: List<MovieUiModel> = emptyList(),
    val error: String = ""
)