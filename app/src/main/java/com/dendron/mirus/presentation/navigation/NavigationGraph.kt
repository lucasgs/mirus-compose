package com.dendron.mirus.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dendron.mirus.common.Constants
import com.dendron.mirus.presentation.movie_detail.MovieDetailScreen
import com.dendron.mirus.presentation.movie_favorite.MovieFavoriteScreen
import com.dendron.mirus.presentation.movie_list.MovieListScreen
import com.dendron.mirus.presentation.movie_search.MovieSearchScreen
import com.dendron.mirus.presentation.ui.theme.MyPurple700
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController, paddingValues: PaddingValues, modifier: Modifier = Modifier
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.MovieListScreen.route,
        modifier = modifier
            .background(MyPurple700)
            .padding(paddingValues),
    ) {
        composable(
            route = Screen.MovieListScreen.route
        ) {
            MovieListScreen(navController = navController)
        }

        composable(
            route = Screen.MovieDetailScreen.route + "/{movieId}",
            arguments = listOf(
                navArgument(Constants.MOVIE_ID_KEY) {
                    type = NavType.StringType
                },
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Up,
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