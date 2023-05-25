package com.dendron.mirus.data.remote

import com.dendron.mirus.data.local.remote.dto.ResultsDto
import com.dendron.mirus.data.local.remote.dto.MovieDetailDto
import com.dendron.mirus.data.remote.dto.ResultDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBApi {

    @GET("discover/movie")
    suspend fun getDiscoverMovies(): ResultsDto<ResultDto>

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): ResultsDto<ResultDto>
    @GET("movie/{id}")
    suspend fun getMovie(@Path("id") movieId: String): MovieDetailDto

//    @GET("genre/movie/list")
//    suspend fun getMovieGenres(): List<GenreDto>

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): ResultsDto<ResultDto>

//    @GET("movie/upcoming")
//    suspend fun getUpcomingMovies(): ResultsDto<ResultDto>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(): ResultsDto<ResultDto>

}