package com.dendron.mirus.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dendron.mirus.common.Constants
import com.dendron.mirus.presentation.movie_detail.MovieDetailScreen
import com.dendron.mirus.presentation.movie_favorite.MovieFavoriteScreen
import com.dendron.mirus.presentation.movie_list.MovieListScreen

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
            MovieFavoriteScreen(navController = navController)
        }

        composable(
            route = Screen.SearchMovie.route,
        ) {
            Text("Search")
        }
    }
}