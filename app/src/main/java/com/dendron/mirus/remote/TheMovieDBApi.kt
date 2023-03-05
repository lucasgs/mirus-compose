package com.dendron.mirus.remote

import com.dendron.mirus.remote.dto.ResultsDto
import com.dendron.mirus.remote.dto.GenreDto
import com.dendron.mirus.remote.dto.ResultDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBApi {

    @GET("discover/movie")
    suspend fun getDiscoverMovies(): ResultsDto

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): ResultsDto

    @GET("movie/{id}")
    suspend fun getMovie(@Path("id") movieId: String): ResultDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): List<GenreDto>

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): ResultsDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(): ResultsDto

}