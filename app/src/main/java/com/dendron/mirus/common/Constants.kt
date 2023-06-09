package com.dendron.mirus.common

import com.dendron.mirus.BuildConfig

object Constants {
    var tmdbApiKey: String = BuildConfig.TMDB_API_KEY
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w780"
    const val TMDB_BACKDROP_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w1280"

    const val MOVIE_ID_KEY = "movieId"
    const val DB_NAME = "mirus-database"

}