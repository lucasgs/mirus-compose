package com.dendron.mirus.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "top_rated")
data class TopRatedEntity(
    @PrimaryKey @ColumnInfo("movieId") val movieId: Int,
)