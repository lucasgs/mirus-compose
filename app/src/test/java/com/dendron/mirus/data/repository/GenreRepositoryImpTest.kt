package com.dendron.mirus.data.repository

import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.data.local.FavoriteDao
import com.dendron.mirus.data.local.model.FavoriteEntity
import com.dendron.mirus.data.local.model.FavoriteMovie
import com.dendron.mirus.data.local.model.MovieEntity
import com.dendron.mirus.domain.model.Movie
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
class GenreRepositoryImpTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var favoriteDao: FavoriteDao

    private lateinit var favoriteRepositoryImp: FavoriteMovieRepositoryImp

    @Before
    fun setUp() {
        favoriteRepositoryImp = FavoriteMovieRepositoryImp(favoriteDao)
    }

    @Test
    fun `getFavoriteMovies should return empty data if api returns empty`() = runTest {

        whenever(favoriteDao.getFavorites()).thenReturn(
            flowOf(
                emptyList()
            )
        )

        val result = favoriteRepositoryImp.getFavoriteMovies().first()
        assert(result.isEmpty())
        verify(favoriteDao).getFavorites()
    }

    @Test
    fun `getFavoriteMovies should return data if dao does return data`() = runTest {

        whenever(favoriteDao.getFavorites()).thenReturn(
            flowOf(
                favoriteMovies
            )
        )

        val movies = favoriteRepositoryImp.getFavoriteMovies().first()
        assert(movies.size == 2)
    }

    @Test
    fun `isFavoriteMovie should return true if movie is favorite`() = runTest {

        whenever(favoriteDao.isFavorite(movie.id)).thenReturn(
            true
        )

        val isFav = favoriteRepositoryImp.isFavoriteMovie(movie)
        assert(isFav)
    }

    @Test
    fun `isFavoriteMovie should return false if movie is not favorite`() = runTest {

        whenever(favoriteDao.isFavorite(movie.id)).thenReturn(
            false
        )

        val isFav = favoriteRepositoryImp.isFavoriteMovie(movie)
        assert(!isFav)
    }

    @Test
    fun `removeFavoriteMovie should call delete on dao`() = runTest {
        favoriteRepositoryImp.removeFavoriteMovie(movie)
        verify(favoriteDao).delete(movie.id)
    }

    @Test
    fun `saveFavoriteMovie should call delete on dao`() = runTest {
        favoriteRepositoryImp.saveFavoriteMovie(movie)
        verify(favoriteDao).insert(any())
    }

    companion object {
        private val movieEntities = listOf(
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
            ), MovieEntity(
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

        val favoriteMovies = listOf(
            FavoriteMovie(
                favoriteEntity = FavoriteEntity(movieId = 1), movie = movieEntities.first()
            ), FavoriteMovie(
                favoriteEntity = FavoriteEntity(movieId = 2), movie = movieEntities[1]
            )

        )

        val movie = Movie(
            id = 1,
            overview = "overview",
            popularity = 1.0,
            voteAverage = 1.0,
            posterPath = "posterPath",
            releaseDate = "releaseDate",
            title = "title",
            backDropPath = "backDropPath",
            genres = listOf(1, 2)
        )
    }
}