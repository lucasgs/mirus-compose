package com.dendron.mirus.ui.movie_search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dendron.mirus.R
import com.dendron.mirus.presentation.movie_search.MovieSearchViewModel
import com.dendron.mirus.ui.components.EmptySpace
import com.dendron.mirus.ui.components.VerticalSection
import com.dendron.mirus.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MovieSearchScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MovieSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun navigateToDetailScreen(movieId: Int) {
        coroutineScope.launch {
            navController.navigate(Screen.MovieDetailScreen.createRoute(movieId))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            EmptySpace(height = 16.dp)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    placeholder = {
                        Text(text = stringResource(R.string.search))
                    },
                    value = state.query,
                    onValueChange = viewModel::onQueryChanged,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            modifier = Modifier
                                .background(Color.Transparent)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.onSearchSubmitted()
                        keyboardController?.hide()
                    }),
                    modifier = Modifier
                        .background(Color.White)
                        .focusRequester(focusRequester)
                )
            }
            when {
                state.query.isBlank() && state.recentSearches.isNotEmpty() -> {
                    EmptySpace(height = 16.dp)
                    Text(
                        text = stringResource(R.string.recent_searches),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    EmptySpace(height = 8.dp)
                    Column {
                        state.recentSearches.forEach { query ->
                            Text(
                                text = query,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onRecentSearchSelected(query) }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                state.movies.isEmpty() && !state.isLoading && state.error.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = if (state.query.isBlank()) {
                                stringResource(R.string.start_typing_to_search)
                            } else {
                                stringResource(R.string.nothing_to_show)
                            },
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }

                else -> {
                    VerticalSection(
                        movies = state.movies,
                        showTitles = false,
                        onItemClick = { movieId ->
                            navigateToDetailScreen(movieId)
                        }
                    )
                }
            }
        }
        if (state.error.isNotEmpty()) {
            Text(
                text = state.error,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}
