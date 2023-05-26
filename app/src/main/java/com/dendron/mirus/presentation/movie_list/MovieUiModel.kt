package com.dendron.mirus.presentation.movie_list

import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.model.Movie

data class MovieUiModel(
    val movie: Movie,
    val isFavorite: Boolean,
    val genres: List<Genre>
)