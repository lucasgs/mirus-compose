package com.dendron.mirus.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.dendron.mirus.common.Constants

sealed class Screen(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object MovieListScreen :
        Screen("Home", Icons.Default.Home, "movie_list_screen")

    object MovieDetailScreen :
        Screen("", Icons.Default.List, "movie_detail_screen") {
        const val MOVIE_ID_ARG = Constants.MOVIE_ID_KEY
        const val ROUTE = "movie_detail_screen"
        val routePattern = "$ROUTE/{$MOVIE_ID_ARG}"

        fun createRoute(movieId: Int): String = "$ROUTE/$movieId"
    }

    object FavoriteMovieScreen :
        Screen("Favorites", Icons.Default.Favorite, "favorite_movie_screen")

    object SearchMovie :
        Screen("Search", Icons.Default.Search, "search_movie")

    companion object {
        val topLevelDestinations: List<Screen>
            get() = listOf(MovieListScreen, SearchMovie, FavoriteMovieScreen)

        val topLevelRoutes: Set<String>
            get() = topLevelDestinations.map { it.route }.toSet()
    }
}
