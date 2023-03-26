package com.dendron.mirus

import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.model.Movie

val movies = listOf(
    Movie(
        id = 1,
        overview = "overview1",
        popularity = 1.0,
        posterPath = "posterPath1",
        releaseDate = "2023-01-01",
        title = "title 1",
        backDropPath = "backDrop 1",
        voteAverage = 1.0,
        genres = listOf(
            Genre(id = 1, name = "Comedy")
        )
    )
)