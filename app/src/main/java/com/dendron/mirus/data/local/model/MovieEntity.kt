package com.dendron.mirus.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class MovieEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "overview") val overview: String,
    @ColumnInfo(name = "popularity") val popularity: Double,
    @ColumnInfo(name = "posterPath") val posterPath: String,
    @ColumnInfo(name = "releaseDate") val releaseDate: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "backDropPath") val backDropPath: String,
    @ColumnInfo(name = "voteAverage") val voteAverage: Double,
    @ColumnInfo(name = "genreIds") val genreIds: String,
)