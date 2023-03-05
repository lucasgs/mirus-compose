package com.dendron.mirus.presentation.movie_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Constants
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.use_case.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(MovieDetailState())
    val state: State<MovieDetailState> = _state

    init {
        savedStateHandle.get<String>(Constants.MOVIE_ID_KEY)?.let { movieId ->
            getMovies(movieId = movieId)
        }
    }

    private fun getMovies(movieId: String) {
        getMovieDetailsUseCase(movieId).onEach { result ->
            when(result){
                is Resource.Success -> _state.value = MovieDetailState(movie = result.data)
                is Resource.Error -> _state.value = MovieDetailState(error = result.message ?: "An expected error occurred")
                is Resource.Loading -> _state.value = MovieDetailState(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}