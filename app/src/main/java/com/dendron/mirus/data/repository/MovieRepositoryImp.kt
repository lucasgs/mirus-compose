package com.dendron.mirus.data.repository

import androidx.room.withTransaction
import com.dendron.mirus.common.Constants
import com.dendron.mirus.data.local.AppDatabase
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
    private val api: TheMovieDBApi,
    private val appDatabase: AppDatabase
) : MovieRepository {

    override fun getDiscoverMovies(): Flow<List<Movie>> =
        appDatabase.movieDao().getDiscoveryMovies()
            .map { movies -> movies.map { item -> item.movie.toDomain() } }

    override suspend fun syncMovies() {
        withContext(Dispatchers.IO) {
            syncTopRatedMovies()
            syncDiscoveryMovies()
            syncTrendingMovies()
        }
    }

    private suspend fun syncDiscoveryMovies() {
        val currentMovies = api.getDiscoverMovies().resultDto.map { it.toDomain() }
        appDatabase.withTransaction {
            appDatabase.movieDao().run {
                clearDiscovery()
                upsertMovies(currentMovies.map(Movie::toEntity))
                upsertDiscovery(currentMovies.map { movie -> DiscoveryEntity(movieId = movie.id) })
            }
        }
    }

    override fun getTopRatedMovies(): Flow<List<Movie>> =
        appDatabase.movieDao().getTopRatedMovies()
            .map { movies -> movies.map { item -> item.movie.toDomain() } }

    private suspend fun syncTopRatedMovies() {
        val currentMovies = api.getTopRatedMovies().resultDto.map { it.toDomain() }
        appDatabase.withTransaction {
            appDatabase.movieDao().run {
                clearTopRated()
                upsertMovies(currentMovies.map(Movie::toEntity))
                upsertTopRated(currentMovies.map { movie -> TopRatedEntity(movieId = movie.id) })
            }
        }
    }

    private suspend fun syncTrendingMovies() {
        val currentMovies = api.getTrendingMovies().resultDto.map { it.toDomain() }
        appDatabase.withTransaction {
            appDatabase.movieDao().run {
                clearTrending()
                upsertMovies(currentMovies.map(Movie::toEntity))
                upsertTrending(currentMovies.map { movie -> TrendingEntity(movieId = movie.id) })
            }
        }
    }

    override fun getTrendingMovies(): Flow<List<Movie>> =
        appDatabase.movieDao().getTrendingMovies()
            .map { movies -> movies.map { item -> item.movie.toDomain() } }

    override fun getMovieDetails(movieId: String): Flow<Movie> = flow {
        syncMovieDetails(movieId)
        emit(appDatabase.movieDao().getMovieDetail(movieId.toInt()).toDomain())
    }

    private suspend fun syncMovieDetails(movieId: String) {
        runCatching {
            api.getMovie(movieId).toMovieDetail()
        }.onSuccess { movie ->
            withContext(Dispatchers.IO) {
                appDatabase.movieDao().upsertMovie(movie.toEntity())
            }
        }
    }

    override fun searchMovies(query: String): Flow<List<Movie>> = flow {
        runCatching {
            api.searchMovies(query).resultDto.map { it.toDomain() }
        }.onFailure {
            emitAll(
                appDatabase.movieDao().searchMovies(query)
                    .map { movies -> movies.map { movie -> movie.toDomain() } }
            )
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
