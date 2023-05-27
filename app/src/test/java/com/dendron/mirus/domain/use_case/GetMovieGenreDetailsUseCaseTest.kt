package com.dendron.mirus.domain.use_case

import app.cash.turbine.test
import com.dendron.mirus.MainDispatcherRule
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Genre
import com.dendron.mirus.domain.repository.GenreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
class GetMovieGenreDetailsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var genreRepository: GenreRepository

    private lateinit var getMovieGenreDetailsUseCase: GetMovieGenreDetailsUseCase

    @Before
    fun setUp() {
        getMovieGenreDetailsUseCase = GetMovieGenreDetailsUseCase(genreRepository)
    }

    @Test
    fun `invoke should call repository to get genre details`() = runTest {
        getMovieGenreDetailsUseCase.invoke(genreIds).collect()
        verify(genreRepository).getGenreDetails(genreIds)
    }

    @Test
    fun `invoke should return loading and success when the repository returns data`() = runTest {
        val expectedLoading = Resource.Loading<Resource<Genre>>()
        val expectedSuccess = Resource.Success(data = genres)

        whenever(genreRepository.getGenreDetails(any())).thenReturn(
            flowOf(genres)
        )

        getMovieGenreDetailsUseCase(genreIds).test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedSuccess, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when the repository returns exception`() = runTest {
        val expectedErrorMessage = "error"
        val expectedLoading = Resource.Loading<List<Genre>>()
        val expectedError = Resource.Error<List<Genre>>(expectedErrorMessage)

        whenever(genreRepository.getGenreDetails(any())).thenAnswer {
            throw Exception(expectedErrorMessage)
        }

        getMovieGenreDetailsUseCase(genreIds).test {
            assertEquals(expectedLoading, awaitItem())
            assertEquals(expectedError, awaitItem())
            awaitComplete()
        }
    }

    companion object {
        private val genreIds = listOf(1, 2, 3)
        private val genres = listOf(
            Genre(
                id = 1,
                name = "genre1"
            ),
            Genre(
                id = 2,
                name = "genre2"
            )
        )
    }
}