package com.dendron.mirus.data.device

import androidx.work.BackoffPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import com.dendron.mirus.common.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncWorkSchedulerTest {

    @Test
    fun `scheduleRecurringMovieSync enqueues uniquely configured periodic work`() {
        val fakeEnqueuer = FakeSyncWorkEnqueuer()
        val scheduler = SyncWorkScheduler(fakeEnqueuer)

        scheduler.scheduleRecurringMovieSync()

        val workRequest = fakeEnqueuer.enqueuedWorkRequest
        assertNotNull(workRequest)
        requireNotNull(workRequest)
        assertTrue(workRequest.tags.contains(Constants.MOVIE_SYNC_PERIODIC_TAG))
        assertEquals(NetworkType.CONNECTED, workRequest.workSpec.constraints.requiredNetworkType)
        assertEquals(BackoffPolicy.EXPONENTIAL, workRequest.workSpec.backoffPolicy)
        assertEquals(30L * 60L * 1000L, workRequest.workSpec.backoffDelayDuration)
    }

    private class FakeSyncWorkEnqueuer : SyncWorkEnqueuer {
        var enqueuedWorkRequest: PeriodicWorkRequest? = null

        override fun enqueue(workRequest: PeriodicWorkRequest) {
            enqueuedWorkRequest = workRequest
        }
    }
}
