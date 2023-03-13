package com.dendron.mirus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dendron.mirus.common.Constants
import com.dendron.mirus.presentation.movie_detail.MovieDetailScreen
import com.dendron.mirus.presentation.movie_list.MovieListScreen
import com.dendron.mirus.presentation.ui.theme.MirusTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    MirusTheme {
        val navController = rememberNavController()
        val screens = listOf(Screen.MovieListScreen, Screen.FavoriteMovieScreen, Screen.SearchMovie)
        val showBottomBar =
            navController.currentBackStackEntryAsState().value?.destination?.route in screens.map { it.route }
        Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
            if (showBottomBar) {
                MainBottomNavigation(navController = navController)
            }
        }) { innerPadding ->
            NavigationGraph(navController = navController, innerPadding)
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController, paddingValues: PaddingValues, modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MovieListScreen.route,
        modifier = modifier.padding(paddingValues)
    ) {
        composable(
            route = Screen.MovieListScreen.route
        ) {
            MovieListScreen(navController = navController)
        }

        composable(
            route = Screen.MovieDetailScreen.route + "/{movieId}",
            arguments = listOf(navArgument(Constants.MOVIE_ID_KEY) {
                type = NavType.StringType
            })
        ) {
            MovieDetailScreen(navController = navController)
        }

        composable(
            route = Screen.FavoriteMovieScreen.route,
        ) {
            Text("Favorites")
        }

        composable(
            route = Screen.SearchMovie.route,
        ) {
            Text("Search")
        }
    }
}