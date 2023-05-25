package com.dendron.mirus.data.repository

import com.dendron.mirus.common.Constants
import com.dendron.mirus.data.local.AppDatabase
import com.dendron.mirus.data.local.model.MovieEntity
import com.dendron.mirus.data.local.remote.dto.toMovieDetail
import com.dendron.mirus.data.remote.TheMovieDBApi
import com.dendron.mirus.data.remote.dto.ResultDto
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieRepositoryImp @Inject constructor(
    private val api: TheMovieDBApi, private val appDatabase: AppDatabase
) : MovieRepository {

    override suspend fun getDiscoverMovies(): Flow<List<Movie>> = flow {
        syncDiscoveryMovies()
        emitAll(appDatabase.movieDao().getMovies()
            .map { it.map { movieEntity -> movieEntity.toDomain() } })
    }

    private suspend fun syncDiscoveryMovies() {
        runCatching {
            api.getDiscoverMovies().resultDto.map { it.toDomain() }
        }.onSuccess { currentMovies ->
            withContext(Dispatchers.IO) {
                currentMovies.forEach { movie ->
                    appDatabase.movieDao().insert(movie.toEntity())
                }
            }
        }
    }

    override suspend fun getTopRatedMovies(): List<Movie> {
        return api.getTopRatedMovies().resultDto.map { it.toDomain() }
    }

    override suspend fun getTrendingMovies(): List<Movie> {
        return api.getTrendingMovies().resultDto.map { it.toDomain() }
    }

    override suspend fun getMovieDetails(movieId: String): Movie {
        return api.getMovie(movieId).toMovieDetail()
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return api.searchMovies(query).resultDto.map { it.toDomain() }
    }
}

fun Movie.toEntity(): MovieEntity = MovieEntity(
    id = id,
    overview = overview,
    popularity = popularity,
    voteAverage = voteAverage,
    posterPath = posterPath,
    releaseDate = releaseDate,
    title = title,
    backDropPath = backDropPath,
)

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    overview = overview,
    popularity = popularity,
    voteAverage = voteAverage,
    posterPath = posterPath,
    releaseDate = releaseDate,
    title = title,
    backDropPath = backDropPath,
    genres = emptyList()
)

fun ResultDto.toDomain(): Movie {
    return Movie(id = id,
        overview = overview,
        popularity = popularity,
        voteAverage = voteAverage,
        posterPath = "${Constants.TMDB_IMAGE_BASE_URL}$posterPath",
        releaseDate = releaseDate,
        title = title,
        backDropPath = "${Constants.TMDB_BACKDROP_IMAGE_BASE_URL}$backdropPath",
        genres = genreIds?.map { Genre(id = it, "") } ?: emptyList())
}
