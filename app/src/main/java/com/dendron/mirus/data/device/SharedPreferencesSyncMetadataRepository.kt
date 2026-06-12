package com.dendron.mirus.data.device

import android.content.SharedPreferences
import com.dendron.mirus.common.Constants
import com.dendron.mirus.domain.repository.SyncMetadataRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SharedPreferencesSyncMetadataRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : SyncMetadataRepository {

    private val _lastSuccessfulSyncAtMillis = MutableStateFlow(readLastSuccessfulSync())
    override val lastSuccessfulSyncAtMillis: Flow<Long?> = _lastSuccessfulSyncAtMillis.asStateFlow()

    override suspend fun recordSuccessfulSync(timestampMillis: Long) {
        sharedPreferences.edit()
            .putLong(Constants.LAST_SUCCESSFUL_SYNC_AT_KEY, timestampMillis)
            .apply()
        _lastSuccessfulSyncAtMillis.value = timestampMillis
    }

    private fun readLastSuccessfulSync(): Long? {
        val value = sharedPreferences.getLong(Constants.LAST_SUCCESSFUL_SYNC_AT_KEY, NO_SYNC_RECORDED)
        return value.takeIf { it != NO_SYNC_RECORDED }
    }

    private companion object {
        const val NO_SYNC_RECORDED = -1L
    }
}
