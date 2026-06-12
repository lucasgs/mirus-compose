package com.dendron.mirus.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dendron.mirus.data.local.model.DiscoveryEntity
import com.dendron.mirus.data.local.model.FavoriteEntity
import com.dendron.mirus.data.local.model.GenreEntity
import com.dendron.mirus.data.local.model.MovieEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var movieDao: MovieDao
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var genreDao: GenreDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        movieDao = database.movieDao()
        favoriteDao = database.favoriteDao()
        genreDao = database.genreDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun discoveryMovies_returns_joined_movie_relations() = runBlocking {
        movieDao.upsertMovies(
            listOf(
                movieEntity(id = 1, title = "Discovery One"),
                movieEntity(id = 2, title = "Discovery Two")
            )
        )
        movieDao.upsertDiscovery(
            listOf(
                DiscoveryEntity(movieId = 1),
                DiscoveryEntity(movieId = 2)
            )
        )

        val result = movieDao.getDiscoveryMovies().first()

        assertEquals(listOf(1, 2), result.map { it.movie.id })
        assertEquals(listOf("Discovery One", "Discovery Two"), result.map { it.movie.title })
    }

    @Test
    fun clearDiscovery_removes_stale_section_mappings_before_refresh() = runBlocking {
        movieDao.upsertMovies(
            listOf(
                movieEntity(id = 1, title = "Old One"),
                movieEntity(id = 2, title = "Old Two"),
                movieEntity(id = 3, title = "Fresh")
            )
        )
        movieDao.upsertDiscovery(
            listOf(
                DiscoveryEntity(movieId = 1),
                DiscoveryEntity(movieId = 2)
            )
        )

        movieDao.clearDiscovery()
        movieDao.upsertDiscovery(listOf(DiscoveryEntity(movieId = 3)))

        val result = movieDao.getDiscoveryMovies().first()

        assertEquals(listOf(3), result.map { it.movie.id })
        assertEquals(listOf("Fresh"), result.map { it.movie.title })
    }

    @Test
    fun favorites_returns_related_movies() = runBlocking {
        movieDao.upsertMovie(movieEntity(id = 7, title = "Favorite"))
        favoriteDao.insert(FavoriteEntity(movieId = 7))

        val result = favoriteDao.getFavorites().first()

        assertEquals(1, result.size)
        assertEquals(7, result.single().movie.id)
        assertEquals("Favorite", result.single().movie.title)
        assertTrue(favoriteDao.isFavorite(movieId = 7))
    }

    @Test
    fun getGenres_with_ids_returns_only_requested_genres() = runBlocking {
        genreDao.insertGenre(GenreEntity(id = 1, name = "Action"))
        genreDao.insertGenre(GenreEntity(id = 2, name = "Comedy"))
        genreDao.insertGenre(GenreEntity(id = 3, name = "Drama"))

        val result = genreDao.getGenres(1, 3).first()

        assertEquals(listOf(1, 3), result.map { it.id }.sorted())
        assertEquals(listOf("Action", "Drama"), result.sortedBy { it.id }.map { it.name })
    }

    private fun movieEntity(id: Int, title: String) = MovieEntity(
        id = id,
        overview = "Overview for $title",
        popularity = id.toDouble(),
        posterPath = "/poster/$id",
        releaseDate = "2024-01-0$id",
        title = title,
        backDropPath = "/backdrop/$id",
        voteAverage = 7.5,
        genreIds = "1,2"
    )
}
