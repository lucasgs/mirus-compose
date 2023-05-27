package com.dendron.mirus.presentation.movie_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.dendron.mirus.presentation.components.rememberLifecycleEvent
import com.dendron.mirus.presentation.movie_list.components.DiscoverySection
import com.dendron.mirus.presentation.movie_list.components.TopRatedSection
import com.dendron.mirus.presentation.movie_list.components.TrendingSection
import com.dendron.mirus.presentation.navigation.Screen
import com.dendron.mirus.presentation.ui.theme.MyPurple700
import kotlinx.coroutines.launch

@Composable
fun MovieListScreen(
    navController: NavController,
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val discoverState = viewModel.discoverMovies.collectAsStateWithLifecycle()
    val topRatedState = viewModel.topRatedMovies.collectAsStateWithLifecycle()
    val trendingState = viewModel.trendingMovies.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isError = viewModel.isError.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val lifecycleEvent = rememberLifecycleEvent()

    LaunchedEffect(lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            viewModel.refresh()
        }
    }

    fun navigateToDetailScreen(movieId: Int) {
        coroutineScope.launch {
            navController.navigate(Screen.MovieDetailScreen.route + "/$movieId")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPurple700)
            .padding(4.dp)
    ) {
        Column {
            TopRatedSection(
                movies = topRatedState.value.movies,
                onNavigateToDetailScreen = ::navigateToDetailScreen
            )
            DiscoverySection(
                movies = discoverState.value.movies,
                onNavigateToDetailScreen = ::navigateToDetailScreen
            )
            TrendingSection(
                movies = trendingState.value.movies,
                onNavigateToDetailScreen = ::navigateToDetailScreen
            )
        }
        if (isError.value.isNotEmpty()) {
            Text(
                text = isError.value.joinToString(),
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }
        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

