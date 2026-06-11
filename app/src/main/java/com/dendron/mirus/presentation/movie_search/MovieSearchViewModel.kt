package com.dendron.mirus.presentation.movie_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dendron.mirus.common.Resource
import com.dendron.mirus.domain.model.Movie
import com.dendron.mirus.domain.use_case.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MovieSearchState())
    val state = _state.asStateFlow()

    init {
        observeQueryChanges()
    }

    fun onQueryChanged(query: String) {
        _state.update { oldState ->
            oldState.copy(query = query)
        }
    }

    fun onSearchSubmitted() {
        addRecentSearch(_state.value.query)
    }

    fun onRecentSearchSelected(query: String) {
        _state.update { oldState ->
            oldState.copy(query = query)
        }
        addRecentSearch(query)
    }

    private fun observeQueryChanges() {
        _state
            .map { state -> state.query.trim() }
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(SearchResult.Empty)
                } else {
                    searchMoviesUseCase(query).map { resource ->
                        SearchResult.Result(resource = resource)
                    }
                }
            }
            .onEach { result ->
                when (result) {
                    SearchResult.Empty -> {
                        _state.update { oldState ->
                            oldState.copy(
                                isLoading = false,
                                movies = emptyList(),
                                error = ""
                            )
                        }
                    }

                    is SearchResult.Result -> {
                        when (val resource = result.resource) {
                            is Resource.Success -> {
                                _state.update { oldState ->
                                    oldState.copy(
                                        isLoading = false,
                                        movies = resource.data.orEmpty(),
                                        error = ""
                                    )
                                }
                            }

                            is Resource.Error -> {
                                _state.update { oldState ->
                                    oldState.copy(
                                        isLoading = false,
                                        movies = emptyList(),
                                        error = resource.message ?: "An expected error occurred"
                                    )
                                }
                            }

                            is Resource.Loading -> {
                                _state.update { oldState ->
                                    oldState.copy(
                                        isLoading = true,
                                        movies = emptyList(),
                                        error = ""
                                    )
                                }
                            }
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addRecentSearch(query: String) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) {
            return
        }

        _state.update { oldState ->
            oldState.copy(
                recentSearches = listOf(normalizedQuery) + oldState.recentSearches
                    .filterNot { recentSearch -> recentSearch.equals(normalizedQuery, ignoreCase = true) }
                    .take(MAX_RECENT_SEARCHES - 1)
            )
        }
    }

    private sealed interface SearchResult {
        data object Empty : SearchResult
        data class Result(
            val resource: Resource<List<Movie>>,
        ) : SearchResult
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
        const val MAX_RECENT_SEARCHES = 5
    }
}
