package com.dendron.mirus.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorite(
    @PrimaryKey @ColumnInfo(name = "movie_id") val id: Int,
    @ColumnInfo(name = "movie_title") val title: String,
    @ColumnInfo(name = "movie_poster_path") val posterPath: String,
)