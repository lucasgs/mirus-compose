package com.dendron.mirus.presentation.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.GetDiscoverMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTopRatedMoviesUseCase
import com.dendron.mirus.domain.use_case.GetTrendingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MovieListState())
    val state = _state.asStateFlow()

    private val _topRatedMovies = MutableStateFlow(MovieListState())
    val topRatedMovies = _topRatedMovies.asStateFlow()

    private val _trendingMovies = MutableStateFlow(MovieListState())
    val trendingMovies = _trendingMovies.asStateFlow()

    init {
        getTopRatedMovies()
        getDiscoveryMovies()
        getTrendingMovies()
    }

    private fun getDiscoveryMovies() {
        getDiscoverMoviesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> _state.value =
                    MovieListState(movies = result.data ?: emptyList())
                is Resource.Error -> _state.value =
                    MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _state.value = MovieListState(isLoading = true)
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

    private fun getTrendingMovies(){
        getTrendingMoviesUseCase().onEach { result ->
            when(result) {
                is Resource.Success -> _trendingMovies.value =
                    MovieListState(movies = result.data ?: emptyList())
                is Resource.Error -> _trendingMovies.value =
                    MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _trendingMovies.value = MovieListState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}