package com.dendron.mirus.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class DiscoveryMovie(
    @Embedded val discoveryEntity: DiscoveryEntity,

    @Relation(
        parentColumn = "movieId",
        entityColumn = "id"
    )
    val movie: MovieEntity,
)