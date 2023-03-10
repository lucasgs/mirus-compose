package com.dendron.mirus.presentation.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.GetDiscoverMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTopRatedMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTrendingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
) : ViewModel() {

    private val _discoverMovies = MutableStateFlow(MovieListState())
    val discoverMovies = _discoverMovies.asStateFlow()

    private val _topRatedMovies = MutableStateFlow(MovieListState())
    val topRatedMovies = _topRatedMovies.asStateFlow()

    private val _trendingMovies = MutableStateFlow(MovieListState())
    val trendingMovies = _trendingMovies.asStateFlow()

    val isError =
        combine(_discoverMovies, _topRatedMovies, _trendingMovies) { discover, topRated, trending ->
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
        }

    init {
        getTopRatedMovies()
        getDiscoverMovies()
        getTrendingMovies()
    }

    private fun getDiscoverMovies() {
        getDiscoverMoviesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> _discoverMovies.value =
                    MovieListState(movies = result.data ?: emptyList())
                is Resource.Error -> _discoverMovies.value =
                    MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _discoverMovies.value = MovieListState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    private fun getTopRatedMovies() {
        getTopRatedMoviesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> _topRatedMovies.value =
                    MovieListState(movies = result.data ?: emptyList())
                is Resource.Error -> _topRatedMovies.value =
                    MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _topRatedMovies.value = MovieListState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    private fun getTrendingMovies() {
        getTrendingMoviesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> _trendingMovies.value =
                    MovieListState(movies = result.data ?: emptyList())
                is Resource.Error -> _trendingMovies.value =
                    MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _trendingMovies.value = MovieListState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}