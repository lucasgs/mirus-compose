package com.dendron.mirus.remote

import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.remote.dto.MovieDetailDto
import com.dendron.mirus.remote.dto.ResultDto
import com.dendron.mirus.remote.dto.ResultsDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class TheMovieDbRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var api: TheMovieDBApi

    private lateinit var theMovieDbRepository: TheMovieDbRepository

    @Before
    fun setUp() {
        theMovieDbRepository = TheMovieDbRepository(api)
    }


    @Test
    fun `getTopRatedMovies should return emtpy data if api returns empty`() = runTest {

        whenever(api.getTopRatedMovies()).thenReturn(
            ResultsDto(
                page = 1,
                resultDto = emptyList(),
                totalPages = 1,
                totalResults = 0
            )
        )

        theMovieDbRepository.getTopRatedMovies()
        verify(api).getTopRatedMovies()
    }

    @Test
    fun `getDiscoveryMovies should return empty data if api returns empty`() = runTest {

        whenever(api.getDiscoverMovies()).thenReturn(
            ResultsDto(
                page = 1,
                resultDto = emptyList(),
                totalPages = 1,
                totalResults = 0
            )
        )

        theMovieDbRepository.getDiscoverMovies()
        verify(api).getDiscoverMovies()
    }

    @Test
    fun `getTrendingMovies should return empty data if api returns empty`() = runTest {

        whenever(api.getTrendingMovies()).thenReturn(
            ResultsDto(
                page = 1,
                resultDto = emptyList(),
                totalPages = 1,
                totalResults = 0
            )
        )

        theMovieDbRepository.getTrendingMovies()
        verify(api).getTrendingMovies()
    }

    @Test
    fun `getTopRatedMovies should return data if api does return data`() = runTest {

        whenever(api.getTopRatedMovies()).thenReturn(
            ResultsDto(
                page = 1,
                resultDto = resultsDto,
                totalPages = 1,
                totalResults = 0
            )
        )

        val movies = theMovieDbRepository.getTopRatedMovies()
        assert(movies.size == 2)
    }

    @Test
    fun `getDiscoveryMovies should return data if api does return data`() = runTest {

        whenever(api.getDiscoverMovies()).thenReturn(
            ResultsDto(
                page = 1,
                resultDto = resultsDto.takeLast(1),
                totalPages = 1,
                totalResults = 0
            )
        )

        val movies = theMovieDbRepository.getDiscoverMovies()
        assert(movies.size == 1)
    }

    @Test
    fun `getTrendingMovies should return data if api does return data`() = runTest {

        whenever(api.getTrendingMovies()).thenReturn(
            ResultsDto(
                page = 1,
                resultDto = resultsDto.take(1),
                totalPages = 1,
                totalResults = 0
            )
        )

        val movies = theMovieDbRepository.getTrendingMovies()
        assert(movies.size == 1)
    }

    @Test
    fun `getMovieDetails should return data if api does return data`() = runTest {

        val id = "1"
        whenever(api.getMovie(id)).thenReturn(movieDetailDto)

        theMovieDbRepository.getMovieDetails(id)
        verify(api).getMovie(id)
    }

    @Test
    fun `searchMovies should return data if api does return data`() = runTest {

        val query = "movie"
        whenever(api.searchMovies(query)).thenReturn(
            ResultsDto(
                page = 1,
                resultDto = resultsDto,
                totalPages = 1,
                totalResults = 0
            )
        )

        theMovieDbRepository.searchMovies(query)
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
            ),
            ResultDto(
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
    }
}