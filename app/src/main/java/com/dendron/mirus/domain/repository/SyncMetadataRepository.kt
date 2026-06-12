package com.dendron.mirus.domain.repository

import kotlinx.coroutines.flow.Flow

interface SyncMetadataRepository {
    val lastSuccessfulSyncAtMillis: Flow<Long?>

    suspend fun recordSuccessfulSync(timestampMillis: Long)
}
