package com.dendron.mirus.data.repository

import com.dendron.mirus.common.Constants
import com.dendron.mirus.data.local.MovieDao
import com.dendron.mirus.data.local.model.DiscoveryEntity
import com.dendron.mirus.data.local.model.GenreEntity
import com.dendron.mirus.data.local.model.MovieEntity
import com.dendron.mirus.data.local.model.TopRatedEntity
import com.dendron.mirus.data.local.model.TrendingEntity
import com.dendron.mirus.data.remote.TheMovieDBApi
import com.dendron.mirus.data.remote.dto.GenreDto
import com.dendron.mirus.data.remote.dto.ResultDto
import com.dendron.mirus.data.remote.dto.toMovieDetail
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
    private val api: TheMovieDBApi, private val movieDao: MovieDao
) : MovieRepository {

    override suspend fun getDiscoverMovies(): Flow<List<Movie>> = flow {
        syncDiscoveryMovies()
        emitAll(movieDao.getDiscoveryMovies()
            .map { it.map { item -> item.movie.toDomain() } })
    }

    private suspend fun syncDiscoveryMovies() {
        runCatching {
            api.getDiscoverMovies().resultDto.map { it.toDomain() }
        }.onSuccess { currentMovies ->
            withContext(Dispatchers.IO) {
                currentMovies.forEach { movie ->
                    movieDao.insertDiscovery(
                        DiscoveryEntity(
                            movieId = movie.id
                        )
                    )
                    movieDao.insertMovie(movie.toEntity())
                }
            }
        }
    }

    override suspend fun getTopRatedMovies(): Flow<List<Movie>> = flow {
        syncTopRatedMovies()
        emitAll(movieDao.getTopRatedMovies()
            .map { it.map { item -> item.movie.toDomain() } })
    }

    private suspend fun syncTopRatedMovies() {
        runCatching {
            api.getTopRatedMovies().resultDto.map { it.toDomain() }
        }.onSuccess { currentMovies ->
            withContext(Dispatchers.IO) {
                currentMovies.forEach { movie ->
                    movieDao.insertTopRated(
                        TopRatedEntity(
                            movieId = movie.id
                        )
                    )
                    movieDao.insertMovie(movie.toEntity())
                }
            }
        }
    }

    private suspend fun syncTrendingMovies() {
        runCatching {
            api.getTrendingMovies().resultDto.map { it.toDomain() }
        }.onSuccess { currentMovies ->
            withContext(Dispatchers.IO) {
                currentMovies.forEach { movie ->
                    movieDao.insertTrending(
                        TrendingEntity(
                            movieId = movie.id
                        )
                    )
                    movieDao.insertMovie(movie.toEntity())
                }
            }
        }
    }

    override suspend fun getTrendingMovies(): Flow<List<Movie>> = flow {
        syncTrendingMovies()
        emitAll(movieDao.getTrendingMovies()
            .map { it.map { item -> item.movie.toDomain() } })
    }

    override suspend fun getMovieDetails(movieId: String): Flow<Movie> = flow {
        syncMovieDetails(movieId)
        emit(movieDao.getMovieDetail(movieId.toInt()).toDomain())
    }

    private suspend fun syncMovieDetails(movieId: String) {
        runCatching {
            api.getMovie(movieId).toMovieDetail()
        }.onSuccess { movie ->
            withContext(Dispatchers.IO) {
                movieDao.insertMovie(movie.toEntity())
            }
        }
    }

    override suspend fun searchMovies(query: String): Flow<List<Movie>> = flow {
        runCatching {
            api.searchMovies(query).resultDto.map { it.toDomain() }
        }.onFailure {
            emitAll(
                movieDao.searchMovies(query)
                    .map { movies -> movies.map { movie -> movie.toDomain() } })
        }.onSuccess { movies ->
            emit(movies)
        }
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
    genreIds = genres.joinToString(separator = ",")
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
    genres = genreIds.split(",").map { it.toInt() }
)

fun ResultDto.toDomain(): Movie {
    return Movie(
        id = id,
        overview = overview,
        popularity = popularity,
        voteAverage = voteAverage,
        posterPath = "${Constants.TMDB_IMAGE_BASE_URL}$posterPath",
        releaseDate = releaseDate,
        title = title,
        backDropPath = "${Constants.TMDB_BACKDROP_IMAGE_BASE_URL}$backdropPath",
        genres = genreIds ?: emptyList()
    )
}

fun GenreDto.toEntity(): GenreEntity = GenreEntity(
    id = id,
    name = name
)
