package com.dendron.mirus.remote

import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.data.local.MovieDao
import com.dendron.mirus.data.local.model.DiscoveryEntity
import com.dendron.mirus.data.local.model.DiscoveryMovie
import com.dendron.mirus.data.local.model.MovieEntity
import com.dendron.mirus.data.local.model.TopRatedEntity
import com.dendron.mirus.data.local.model.TopRatedMovie
import com.dendron.mirus.data.local.model.TrendingEntity
import com.dendron.mirus.data.local.model.TrendingMovie
import com.dendron.mirus.data.remote.TheMovieDBApi
import com.dendron.mirus.data.remote.dto.MovieDetailDto
import com.dendron.mirus.data.remote.dto.ResultDto
import com.dendron.mirus.data.remote.dto.ResultsDto
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
import org.mockito.kotlin.any
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
    private lateinit var movieDao: MovieDao

    private lateinit var movieRepositoryImp: MovieRepositoryImp

    @Before
    fun setUp() {
        movieRepositoryImp = MovieRepositoryImp(api, movieDao)
    }

    @Test
    fun `getTopRatedMovies should return emtpy data if api returns empty`() = runTest {

        whenever(api.getTopRatedMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = emptyList(), totalPages = 1, totalResults = 0
            )
        )

        whenever(movieDao.getTopRatedMovies()).thenReturn(
            flowOf(
                emptyList()
            )
        )

        val result = movieRepositoryImp.getTopRatedMovies().first()
        assert(result.isEmpty())
        verify(api).getTopRatedMovies()
        verify(movieDao).getTopRatedMovies()
    }

    @Test
    fun `getDiscoveryMovies should return empty data if api returns empty`() = runTest {

        whenever(api.getDiscoverMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = emptyList(), totalPages = 1, totalResults = 0
            )
        )

        whenever(movieDao.getDiscoveryMovies()).thenReturn(
            flowOf(
                emptyList()
            )
        )

        val result = movieRepositoryImp.getDiscoverMovies().first()
        assert(result.isEmpty())
        verify(api).getDiscoverMovies()
        verify(movieDao).getDiscoveryMovies()
    }

    @Test
    fun `getTrendingMovies should return empty data if api returns empty`() = runTest {

        whenever(api.getTrendingMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = emptyList(), totalPages = 1, totalResults = 0
            )
        )

        whenever(movieDao.getTrendingMovies()).thenReturn(
            flowOf(
                emptyList()
            )
        )

        val result = movieRepositoryImp.getTrendingMovies().first()
        assert(result.isEmpty())
        verify(api).getTrendingMovies()
        verify(movieDao).getTrendingMovies()
    }

    @Test
    fun `getTopRatedMovies should return data if api does return data`() = runTest {

        whenever(api.getTopRatedMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = resultsDto, totalPages = 1, totalResults = 0
            )
        )

        whenever(movieDao.getTopRatedMovies()).thenReturn(
            flowOf(
                topRatedMovies
            )
        )

        val movies = movieRepositoryImp.getTopRatedMovies().first()
        assert(movies.size == 2)
    }

    @Test
    fun `getDiscoveryMovies should return data if api does return data`() = runTest {

        whenever(api.getDiscoverMovies()).thenReturn(
            ResultsDto(
                page = 1, resultDto = resultsDto.takeLast(1), totalPages = 1, totalResults = 0
            )
        )

        whenever(movieDao.getDiscoveryMovies()).thenReturn(
            flowOf(
                discoveryMovies.take(1)
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

        whenever(movieDao.getTrendingMovies()).thenReturn(
            flowOf(
                trendingMovies.take(1)
            )
        )

        val movies = movieRepositoryImp.getTrendingMovies().first()
        assert(movies.size == 1)
    }

    @Test
    fun `getMovieDetails should return data if api does return data`() = runTest {

        val id = "1"
        whenever(api.getMovie(id)).thenReturn(movieDetailDto)

        whenever(movieDao.getMovieDetail(id.toInt())).thenReturn(
            movieEntities.first()
        )

        val result = movieRepositoryImp.getMovieDetails(id).first()
        assert(result.id == movieDetailDto.id)
        verify(api).getMovie(id)
        verify(movieDao).insertMovie(any())
    }

    @Test
    fun `searchMovies should return data if api does return data`() = runTest {

        val query = "movie"
        whenever(api.searchMovies(query)).thenReturn(
            ResultsDto(
                page = 1, resultDto = resultsDto, totalPages = 1, totalResults = 0
            )
        )

        movieRepositoryImp.searchMovies(query).first()
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
                title = "title",
                backDropPath = "backDropPath",
                genreIds = "1,2"
            ),
            MovieEntity(
                id = 2,
                overview = "overview",
                popularity = 2.0,
                voteAverage = 2.0,
                posterPath = "posterPath",
                releaseDate = "releaseDate",
                title = "title",
                backDropPath = "backDropPath",
                genreIds = "1,3"
            )
        )

        private val topRatedEntities = listOf(
            TopRatedEntity(movieId = 1),
            TopRatedEntity(movieId = 2)
        )

        val topRatedMovies = listOf(
            TopRatedMovie(
                topRatedEntity = topRatedEntities.first(),
                movie = movieEntities.first()
            ),
            TopRatedMovie(
                topRatedEntity = topRatedEntities[1],
                movie = movieEntities[1]
            )
        )

        private val trendingEntities = listOf(
            TrendingEntity(movieId = 1),
            TrendingEntity(movieId = 2)
        )

        val trendingMovies = listOf(
            TrendingMovie(
                trendingEntity = trendingEntities.first(),
                movie = movieEntities.first()
            ),
            TrendingMovie(
                trendingEntity = trendingEntities[1],
                movie = movieEntities[1]
            )
        )

        private val discoveryEntities = listOf(
            DiscoveryEntity(movieId = 1),
            DiscoveryEntity(movieId = 2)
        )

        val discoveryMovies = listOf(
            DiscoveryMovie(
                discoveryEntity = discoveryEntities.first(),
                movie = movieEntities.first()
            ),
            DiscoveryMovie(
                discoveryEntity = discoveryEntities[1],
                movie = movieEntities[1]
            ),
        )
    }
}