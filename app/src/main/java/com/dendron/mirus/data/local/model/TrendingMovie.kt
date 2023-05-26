package com.dendron.mirus.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class TrendingMovie(
    @Embedded val trendingEntity: TrendingEntity,

    @Relation(
        parentColumn = "movieId",
        entityColumn = "id"
    )
    val movie: MovieEntity,
)