package com.dendron.mirus.presentation.movie_list

data class MovieSyncStatus(
    val isSyncing: Boolean = false,
    val isOffline: Boolean = false,
    val lastUpdatedAtMillis: Long? = null,
    val errorMessage: String = "",
)
