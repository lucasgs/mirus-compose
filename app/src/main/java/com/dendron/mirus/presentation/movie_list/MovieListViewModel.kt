package com.dendron.mirus.presentation.movie_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.GetDiscoverMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(MovieListState())
    val state: State<MovieListState> = _state

    init {
        getMovies()
    }

    private fun getMovies() {
        getDiscoverMoviesUseCase().onEach { result ->
            when(result){
                is Resource.Success -> _state.value = MovieListState(movies = result.data ?: emptyList())
                is Resource.Error -> _state.value = MovieListState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _state.value = MovieListState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}