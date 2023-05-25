package com.dendron.mirus.remote

import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.data.local.AppDatabase
import com.dendron.mirus.data.local.MovieDao
import com.dendron.mirus.data.local.model.MovieEntity
import com.dendron.mirus.data.local.remote.dto.MovieDetailDto
import com.dendron.mirus.data.local.remote.dto.ResultsDto
import com.dendron.mirus.data.remote.TheMovieDBApi
import com.dendron.mirus.data.remote.dto.ResultDto
import com.dendron.mirus.data.repository.MovieRepositoryImp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryImpTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var api: TheMovieDBApi

    @Mock
    private lateinit var appDatabase: AppDatabase

    @Mock
    private lateinit var movieDao: MovieDao

    private lateinit var movieRepositoryImp: MovieRepositoryImp

    @Before
    fun setUp() {
        movieRepositoryImp = MovieRepositoryImp(api, appDatabase)

        whenever(appDatabase.movieDao()).thenReturn(
            movieDao
        )
    }

    @Test
    fun `getTopRatedMovies should return emtpy data if api returns empty`() = runTest {

        whenever(api.getTopRatedMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = emptyList(), totalPages = 1, totalResults = 0
            )
        )

        movieRepositoryImp.getTopRatedMovies()
        verify(api).getTopRatedMovies()
    }

    @Test
    fun `getDiscoveryMovies should return empty data if api returns empty`() = runTest {

        whenever(api.getDiscoverMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = emptyList(), totalPages = 1, totalResults = 0
            )
        )

        whenever(appDatabase.movieDao().getMovies()).thenReturn(
            flowOf(
                movieEntities
            )
        )

        movieRepositoryImp.getDiscoverMovies().first()
        verify(api).getDiscoverMovies()
    }

    @Test
    fun `getTrendingMovies should return empty data if api returns empty`() = runTest {

        whenever(api.getTrendingMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = emptyList(), totalPages = 1, totalResults = 0
            )
        )

        movieRepositoryImp.getTrendingMovies()
        verify(api).getTrendingMovies()
    }

    @Test
    fun `getTopRatedMovies should return data if api does return data`() = runTest {

        whenever(api.getTopRatedMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = resultsDto, totalPages = 1, totalResults = 0
            )
        )

        val movies = movieRepositoryImp.getTopRatedMovies()
        assert(movies.size == 2)
    }

    @Test
    fun `getDiscoveryMovies should return data if api does return data`() = runTest {

        whenever(api.getDiscoverMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = resultsDto.takeLast(1), totalPages = 1, totalResults = 0
            )
        )

        whenever(appDatabase.movieDao().getMovies()).thenReturn(
            flowOf(
                movieEntities
            )
        )

        val movies = movieRepositoryImp.getDiscoverMovies().first()
        assert(movies.size == 1)
    }

    @Test
    fun `getTrendingMovies should return data if api does return data`() = runTest {

        whenever(api.getTrendingMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = resultsDto.take(1), totalPages = 1, totalResults = 0
            )
        )

        val movies = movieRepositoryImp.getTrendingMovies()
        assert(movies.size == 1)
    }

    @Test
    fun `getMovieDetails should return data if api does return data`() = runTest {

        val id = "1"
        whenever(api.getMovie(id)).thenReturn(movieDetailDto)

        movieRepositoryImp.getMovieDetails(id)
        verify(api).getMovie(id)
    }

    @Test
    fun `searchMovies should return data if api does return data`() = runTest {

        val query = "movie"
        whenever(api.searchMovies(query)).thenReturn(
            ResultsDto(
                page = 1, resultDto = resultsDto, totalPages = 1, totalResults = 0
            )
        )

        movieRepositoryImp.searchMovies(query)
        verify(api).searchMovies(query)
    }

    companion object {
        val movieDetailDto = MovieDetailDto(
            adult = true,
            backdropPath = "backdropPath",
            genres = emptyList(),
            id = 1,
            originalLanguage = "es",
            originalTitle = "originalTitle",
            overview = "This is the overview",
            popularity = 1.0,
            posterPath = "posterPath",
            releaseDate = "2023-01-01",
            title = "title",
            video = true,
            voteAverage = 1.0,
            voteCount = 1,
        )

        val resultsDto = listOf(
            ResultDto(
                adult = true,
                backdropPath = "backdropPath",
                genreIds = listOf(1, 2),
                id = 1,
                originalLanguage = "es",
                originalTitle = "originalTitle",
                overview = "This is the overview",
                popularity = 1.0,
                posterPath = "posterPath",
                releaseDate = "2023-01-01",
                title = "title",
                video = true,
                voteAverage = 1.0,
                voteCount = 1,
            ), ResultDto(
                adult = true,
                backdropPath = "backdropPath",
                genreIds = listOf(1),
                id = 2,
                originalLanguage = "es",
                originalTitle = "originalTitle 2",
                overview = "This is the overview 2",
                popularity = 2.0,
                posterPath = "posterPath 2",
                releaseDate = "2023-01-01",
                title = "title2",
                video = true,
                voteAverage = 2.0,
                voteCount = 2,
            )
        )

        val movieEntities = listOf(
            MovieEntity(
                id = 1,
                overview = "overview",
                popularity = 1.0,
                voteAverage = 1.0,
                posterPath = "posterPath",
                releaseDate = "releaseDate",
                title = "tile",
                backDropPath = "backDropPath",
            )
        )
    }
}