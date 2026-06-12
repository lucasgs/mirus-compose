package com.dendron.mirus.presentation.movie_list

import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.domain.NetworkChecker
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.repository.GenreRepository
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.domain.repository.SyncMetadataRepository
import com.dendron.mirus.domain.use_case.GetDiscoverMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTopRatedMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTrendingMoviesUseCase
import com.dendron.mirus.domain.use_case.SyncMoviesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init should expose offline sync status and last updated timestamp`() = runTest {
        val lastUpdatedAt = 1_234L
        val syncMetadataRepository = FakeSyncMetadataRepository(lastUpdatedAt)
        val viewModel = MovieListViewModel(
            getDiscoverMoviesUseCase = GetDiscoverMoviesUseCase(FakeMovieRepository()),
            getTopRatedMoviesUseCase = GetTopRatedMoviesUseCase(FakeMovieRepository()),
            getTrendingMoviesUseCase = GetTrendingMoviesUseCase(FakeMovieRepository()),
            syncMoviesUseCase = SyncMoviesUseCase(
                networkChecker = FakeNetworkChecker(isOnline = false),
                movieRepository = FakeMovieRepository(),
                genreRepository = FakeGenreRepository(),
                syncMetadataRepository = syncMetadataRepository,
            ),
            syncMetadataRepository = syncMetadataRepository,
        )

        advanceUntilIdle()

        with(viewModel.syncStatus.value) {
            assertTrue(isOffline)
            assertEquals(lastUpdatedAt, lastUpdatedAtMillis)
            assertEquals(SyncMoviesUseCase.OFFLINE_MESSAGE, errorMessage)
            assertFalse(isSyncing)
        }
    }

    @Test
    fun `successful sync should clear offline status and record last updated`() = runTest {
        val syncMetadataRepository = FakeSyncMetadataRepository(null)
        val viewModel = MovieListViewModel(
            getDiscoverMoviesUseCase = GetDiscoverMoviesUseCase(FakeMovieRepository()),
            getTopRatedMoviesUseCase = GetTopRatedMoviesUseCase(FakeMovieRepository()),
            getTrendingMoviesUseCase = GetTrendingMoviesUseCase(FakeMovieRepository()),
            syncMoviesUseCase = SyncMoviesUseCase(
                networkChecker = FakeNetworkChecker(isOnline = true),
                movieRepository = FakeMovieRepository(),
                genreRepository = FakeGenreRepository(),
                syncMetadataRepository = syncMetadataRepository,
            ),
            syncMetadataRepository = syncMetadataRepository,
        )

        advanceUntilIdle()

        with(viewModel.syncStatus.value) {
            assertFalse(isOffline)
            assertTrue(errorMessage.isEmpty())
            assertFalse(isSyncing)
            assertTrue(lastUpdatedAtMillis != null)
        }
    }

    private class FakeMovieRepository : MovieRepository {
        override fun getDiscoverMovies(): Flow<List<Movie>> = flowOf(emptyList())
        override fun getTopRatedMovies(): Flow<List<Movie>> = flowOf(emptyList())
        override fun getTrendingMovies(): Flow<List<Movie>> = flowOf(emptyList())
        override fun getMovieDetails(movieId: String): Flow<Movie> = flowOf(sampleMovie())
        override fun searchMovies(query: String): Flow<List<Movie>> = flowOf(emptyList())
        override suspend fun syncMovies() = Unit
    }

    private class FakeGenreRepository : GenreRepository {
        override fun getGenreDetails(genresId: List<Int>): Flow<List<Genre>> = flowOf(emptyList())
        override fun getGenres(): Flow<List<Genre>> = flowOf(emptyList())
        override suspend fun syncMovieGenres() = Unit
    }

    private class FakeNetworkChecker(
        private val isOnline: Boolean,
    ) : NetworkChecker {
        override fun isOnline(): Boolean = isOnline
    }

    private class FakeSyncMetadataRepository(
        initialValue: Long?,
    ) : SyncMetadataRepository {
        private val state = MutableStateFlow(initialValue)

        override val lastSuccessfulSyncAtMillis: Flow<Long?> = state

        override suspend fun recordSuccessfulSync(timestampMillis: Long) {
            state.value = timestampMillis
        }
    }

    private companion object {
        fun sampleMovie() = Movie(
            id = 1,
            overview = "overview",
            popularity = 1.0,
            posterPath = "poster",
            releaseDate = "2024-01-01",
            title = "title",
            backDropPath = "backdrop",
            voteAverage = 7.0,
            genres = emptyList(),
        )
    }
}
