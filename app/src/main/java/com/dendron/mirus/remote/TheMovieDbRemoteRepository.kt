package com.dendron.mirus.remote

import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.remote.dto.toMovie
import com.dendron.mirus.remote.dto.toMovieDetail
import javax.inject.Inject

class TheMovieDbRemoteRepository @Inject constructor(
    private val api: TheMovieDBApi
): MovieRepository {
    override suspend fun getDiscoverMovies(): List<Movie> {
        return api.getDiscoverMovies().resultDto.map { it.toMovie() }
    }

    override suspend fun getTopRatedMovies(): List<Movie> {
       return api.getTopRatedMovies().resultDto.map { it.toMovie() }
    }

    override suspend fun getTrendingMovies(): List<Movie> {
        return api.getTrendingMovies().resultDto.map { it.toMovie() }
    }

    override suspend fun getMovieDetails(movieId: String): Movie {
        return api.getMovie(movieId).toMovieDetail()
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return api.searchMovies(query).resultDto.map { it.toMovie() }
    }
}