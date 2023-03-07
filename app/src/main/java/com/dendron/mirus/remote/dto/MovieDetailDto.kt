package com.dendron.mirus.remote.dto

import com.dendron.mirus.common.Constants
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.model.Movie
import com.google.gson.annotations.SerializedName

data class MovieDetailDto(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("genres")
    val genres: List<GenreDto>?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("video")
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
)

fun MovieDetailDto.toMovieDetail(): Movie {
    return Movie(
        id = id,
        overview = overview,
        popularity = popularity,
        voteAverage = voteAverage,
        posterPath = "${Constants.TMDB_IMAGE_BASE_URL}$posterPath",
        releaseDate = releaseDate,
        title = title,
        backDropPath = "${Constants.TMDB_BACKDROP_IMAGE_BASE_URL}$backdropPath",
        genres = genres?.map { it.toGenre() } ?: emptyList()
    )
}

fun GenreDto.toGenre(): Genre {
    return Genre(
        id = id,
        name = name,
    )
}
