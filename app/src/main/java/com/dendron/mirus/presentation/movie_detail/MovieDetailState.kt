package com.dendron.mirus.presentation.movie_detail

import com.dendron.mirus.presentation.movie_list.MovieUiModel

data class MovieDetailState(
    val isLoading: Boolean = false,
    val model: MovieUiModel? = null,
    val error: String = ""
)