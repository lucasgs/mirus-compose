package com.dendron.mirus.presentation.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.repository.SyncMetadataRepository
import com.dendron.mirus.domain.use_case.GetDiscoverMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTopRatedMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTrendingMoviesUseCase
import com.dendron.mirus.domain.use_case.SyncMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class MovieListViewModel @Inject constructor(
    getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase,
    getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val syncMoviesUseCase: SyncMoviesUseCase,
    syncMetadataRepository: SyncMetadataRepository,
) : ViewModel() {

    private val _syncStatus = MutableStateFlow(MovieSyncStatus())
    val syncStatus = _syncStatus.asStateFlow()

    val discoverMovies = getDiscoverMoviesUseCase().map { resource ->
        when (resource) {
            is Resource.Error -> MovieListState(error = resource.message.orEmpty())
            is Resource.Loading -> MovieListState(isLoading = true)
            is Resource.Success -> MovieListState(movies = resource.data)
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieListState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    val topRatedMovies = getTopRatedMoviesUseCase().map { resource ->
        when (resource) {
            is Resource.Error -> MovieListState(error = resource.message.orEmpty())
            is Resource.Loading -> MovieListState(isLoading = true)
            is Resource.Success -> MovieListState(movies = resource.data)
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieListState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    val trendingMovies = getTrendingMoviesUseCase().map { resource ->
        when (resource) {
            is Resource.Error -> MovieListState(error = resource.message.orEmpty())
            is Resource.Loading -> MovieListState(isLoading = true)
            is Resource.Success -> MovieListState(movies = resource.data)
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MovieListState(isLoading = true),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )

    val isLoading =
        combine(
            discoverMovies,
            topRatedMovies,
            trendingMovies,
            syncStatus,
        ) { discover, topRated, trending, syncStatus ->
            discover.isLoading || topRated.isLoading || trending.isLoading || syncStatus.isSyncing
        }.stateIn(
            scope = viewModelScope,
            initialValue = false,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
        )

    val isError =
        combine(
            discoverMovies,
            topRatedMovies,
            trendingMovies,
            syncStatus,
        ) { discover, topRated, trending, syncStatus ->
            buildList {
                if (syncStatus.errorMessage.isNotEmpty()) {
                    add(syncStatus.errorMessage)
                }
                if (discover.error.isNotEmpty()) {
                    add(discover.error)
                }
                if (topRated.error.isNotEmpty()) {
                    add(topRated.error)
                }
                if (trending.error.isNotEmpty()) {
                    add(trending.error)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
        )

    init {
        syncMetadataRepository.lastSuccessfulSyncAtMillis
            .onEach { lastUpdatedAtMillis ->
                _syncStatus.update { currentStatus ->
                    currentStatus.copy(lastUpdatedAtMillis = lastUpdatedAtMillis)
                }
            }
            .launchIn(viewModelScope)
        syncData()
    }

    private fun syncData() {
        syncMoviesUseCase()
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _syncStatus.update { currentStatus ->
                            currentStatus.copy(isSyncing = true, errorMessage = "")
                        }
                    }

                    is Resource.Success -> {
                        _syncStatus.update { currentStatus ->
                            currentStatus.copy(isSyncing = false, isOffline = false, errorMessage = "")
                        }
                    }

                    is Resource.Error -> {
                        _syncStatus.update { currentStatus ->
                            currentStatus.copy(
                                isSyncing = false,
                                isOffline = result.message == SyncMoviesUseCase.OFFLINE_MESSAGE,
                                errorMessage = result.message.orEmpty()
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
