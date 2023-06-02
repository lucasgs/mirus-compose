package com.dendron.mirus.presentation.movie_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.dendron.mirus.presentation.ContentType
import com.dendron.mirus.presentation.movie_favorite.MovieFavoriteScreen
import com.dendron.mirus.presentation.movie_list.components.DiscoverySection
import com.dendron.mirus.presentation.movie_list.components.TopRatedSection
import com.dendron.mirus.presentation.movie_list.components.TrendingSection
import com.dendron.mirus.presentation.movie_search.MovieSearchScreen
import com.dendron.mirus.presentation.navigation.Screen
import com.dendron.mirus.presentation.ui.theme.MyPurple700
import kotlinx.coroutines.launch

@Composable
fun MovieListScreen(
    navController: NavHostController,
    contentType: ContentType,
) {
    if (contentType == ContentType.SINGLE_PANE) {
        MovieListWrapper(navController = navController)
    } else if (contentType == ContentType.DUAL_PANE) {
        DrawerMenu(navController = navController)
    }
}

@Composable
fun DrawerMenu(navController: NavHostController) {
    Box(
        modifier = Modifier
            .background(MyPurple700)
            .padding(4.dp)
    ) {

        val items = listOf(
            Screen.MovieListScreen, Screen.SearchMovie, Screen.FavoriteMovieScreen
        )
        val selectedItem = remember { mutableStateOf(items[0]) }

        PermanentNavigationDrawer(drawerContent = {
            PermanentDrawerSheet(
                drawerContainerColor = MyPurple700,
                drawerContentColor = MyPurple700,
                windowInsets = WindowInsets(12.dp),
                modifier = Modifier
                    .width(240.dp)
                    .padding(top = 20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items.forEach { item ->
                        NavigationDrawerItem(icon = { item.icon },
                            label = { Text(item.title) },
                            selected = item == selectedItem.value,
                            onClick = {
                                selectedItem.value = item
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }, content = {
            when (selectedItem.value) {
                Screen.MovieListScreen -> MovieListWrapper(navController = navController)
                Screen.FavoriteMovieScreen -> MovieFavoriteScreen(navController = navController)
                Screen.SearchMovie -> MovieSearchScreen(navController = navController)
                else -> Unit
            }
        })
    }
}

@Composable
fun MovieListWrapper(
    navController: NavController, viewModel: MovieListViewModel = hiltViewModel()
) {
    val discoverState = viewModel.discoverMovies.collectAsStateWithLifecycle()
    val topRatedState = viewModel.topRatedMovies.collectAsStateWithLifecycle()
    val trendingState = viewModel.trendingMovies.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isError = viewModel.isError.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

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
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
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
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

