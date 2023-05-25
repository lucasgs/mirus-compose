package com.dendron.mirus.presentation.movie_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MovieSearchState())
    val state = _state.asStateFlow()

    fun onQueryChanged(query: String) {
        _state.update { oldState ->
            oldState.copy(query = query)
        }
    }

    fun searchMovie() {
        searchMoviesUseCase(query = _state.value.query).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value =
                        MovieSearchState(movies = result.data)
                }

                is Resource.Error -> _state.value =
                    MovieSearchState(error = result.message ?: "An expected error occurred")

                is Resource.Loading -> _state.value = MovieSearchState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}