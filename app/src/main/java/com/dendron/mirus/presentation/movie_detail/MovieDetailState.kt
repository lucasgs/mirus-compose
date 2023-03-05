package com.dendron.mirus.presentation.movie_detail

import com.dendron.mirus.domain.model.Movie

class MovieDetailState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val error: String = ""
)