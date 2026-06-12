package com.dendron.mirus.ui.navigation

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dendron.mirus.presentation.ContentType
import com.dendron.mirus.ui.movie_detail.MovieDetailScreen
import com.dendron.mirus.ui.movie_favorite.MovieFavoriteScreen
import com.dendron.mirus.ui.movie_list.MovieListScreen
import com.dendron.mirus.ui.movie_search.MovieSearchScreen
import com.dendron.mirus.ui.theme.MyPurple700

@Composable
fun NavigationGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    contentType: ContentType,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MovieListScreen.route,
        modifier = modifier
            .background(MyPurple700)
            .padding(paddingValues),
    ) {
        composable(
            route = Screen.MovieListScreen.route
        ) {
            MovieListScreen(navController = navController, contentType = contentType)
        }

        composable(
            route = Screen.MovieDetailScreen.routePattern,
            arguments = listOf(
                navArgument(Screen.MovieDetailScreen.MOVIE_ID_ARG) {
                    type = NavType.StringType
                },
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(700)
                )
            },
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
            MovieSearchScreen(navController = navController)
        }
    }
}