package com.dendron.mirus.domain.use_case

import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.NetworkChecker
import com.dendron.mirus.domain.repository.GenreRepository
import com.dendron.mirus.domain.repository.MovieRepository
import com.dendron.mirus.domain.repository.SyncMetadataRepository
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class SyncMoviesUseCase @Inject constructor(
    private val networkChecker: NetworkChecker,
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository,
    private val syncMetadataRepository: SyncMetadataRepository,
) {
    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        if (!networkChecker.isOnline()) {
            emit(Resource.Error(OFFLINE_MESSAGE))
            return@flow
        }

        runCatching {
            retryWithBackoff { movieRepository.syncMovies() }
            retryWithBackoff { genreRepository.syncMovieGenres() }
            syncMetadataRepository.recordSuccessfulSync(System.currentTimeMillis())
            emit(Resource.Success(true))
        }.onFailure {
            emit(Resource.Error(it.localizedMessage ?: DEFAULT_ERROR_MESSAGE))
        }
    }

    private suspend fun retryWithBackoff(block: suspend () -> Unit) {
        var currentDelayMs = INITIAL_RETRY_DELAY_MS
        repeat(RETRY_ATTEMPTS - 1) {
            try {
                block()
                return
            } catch (throwable: Throwable) {
                if (!throwable.isTransientNetworkFailure()) {
                    throw throwable
                }
            }
            delay(currentDelayMs)
            currentDelayMs = (currentDelayMs * RETRY_MULTIPLIER).toLong()
                .coerceAtMost(MAX_RETRY_DELAY_MS)
        }
        block()
    }

    private fun Throwable.isTransientNetworkFailure(): Boolean = when (this) {
        is IOException -> true
        is HttpException -> code() in TRANSIENT_HTTP_CODES
        else -> false
    }

    companion object {
        const val OFFLINE_MESSAGE = "Offline. Showing cached data."
        private const val DEFAULT_ERROR_MESSAGE = "Unable to refresh movies right now"
        private const val RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY_MS = 1_000L
        private const val MAX_RETRY_DELAY_MS = 4_000L
        private const val RETRY_MULTIPLIER = 2.0
        private val TRANSIENT_HTTP_CODES = setOf(408, 429, 500, 502, 503, 504)
    }
}
