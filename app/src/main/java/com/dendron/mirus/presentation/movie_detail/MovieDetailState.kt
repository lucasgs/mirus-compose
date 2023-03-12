package com.dendron.mirus.presentation.movie_detail

import com.dendron.mirus.presentation.movie_list.MovieUiModel

class MovieDetailState(
    val isLoading: Boolean = false,
    val model: MovieUiModel? = null,
    val error: String = ""
)