package com.dendron.mirus.data.device

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.dendron.mirus.common.Constants
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.SyncMoviesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filterNot

@HiltWorker
class SyncMoviesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncMoviesUseCase: SyncMoviesUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val result = syncMoviesUseCase()
            .filterNot { it is Resource.Loading }
            .first()

        return when (result) {
            is Resource.Success -> Result.success()
            is Resource.Error -> Result.retry()
            is Resource.Loading -> Result.retry()
        }
    }

    companion object {
        private const val SYNC_REPEAT_HOURS = 6L
        private const val BACKOFF_MINUTES = 30L

        fun createWorkRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<SyncMoviesWorker>(
                SYNC_REPEAT_HOURS,
                TimeUnit.HOURS,
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    BACKOFF_MINUTES,
                    TimeUnit.MINUTES,
                )
                .addTag(Constants.MOVIE_SYNC_PERIODIC_TAG)
                .build()
        }
    }
}

fun interface SyncWorkEnqueuer {
    fun enqueue(workRequest: PeriodicWorkRequest)
}

@Singleton
class WorkManagerSyncWorkEnqueuer @Inject constructor(
    private val workManager: WorkManager,
) : SyncWorkEnqueuer {
    override fun enqueue(workRequest: PeriodicWorkRequest) {
        workManager.enqueueUniquePeriodicWork(
            Constants.MOVIE_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest,
        )
    }
}

@Singleton
class SyncWorkScheduler @Inject constructor(
    private val syncWorkEnqueuer: SyncWorkEnqueuer,
) {
    fun scheduleRecurringMovieSync() {
        syncWorkEnqueuer.enqueue(SyncMoviesWorker.createWorkRequest())
    }
}
