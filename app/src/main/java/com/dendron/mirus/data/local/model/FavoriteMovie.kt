package com.dendron.mirus.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class FavoriteMovie(
    @Embedded val favoriteEntity: FavoriteEntity,

    @Relation(
        parentColumn = "movieId",
        entityColumn = "id"
    )
    val movie: MovieEntity,
)