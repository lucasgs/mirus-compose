package com.dendron.mirus.presentation.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.GetDiscoverMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTopRatedMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTrendingMoviesUseCase
import com.dendron.mirus.domain.use_case.SyncMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase,
    getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    val syncMoviesUseCase: SyncMoviesUseCase,
) : ViewModel() {

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
            trendingMovies
        ) { discover, topRated, trending ->
            discover.isLoading || topRated.isLoading || trending.isLoading
        }.stateIn(
            scope = viewModelScope,
            initialValue = false,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
        )

    val isError =
        combine(
            discoverMovies,
            topRatedMovies,
            trendingMovies
        ) { discover, topRated, trending ->
            buildList {
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
        syncData()
    }

    private fun syncData() {
        syncMoviesUseCase().launchIn(viewModelScope)
    }
}