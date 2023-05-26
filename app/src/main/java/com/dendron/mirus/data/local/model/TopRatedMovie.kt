package com.dendron.mirus.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class TopRatedMovie(
    @Embedded val topRatedEntity: TopRatedEntity,

    @Relation(
        parentColumn = "movieId",
        entityColumn = "id"
    )
    val movie: MovieEntity,
)