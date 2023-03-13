package com.dendron.mirus.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val title: String, val icon: ImageVector, val route: String
) {
    object MovieListScreen : Screen("Home", Icons.Default.Home, "movie_list_screen")
    object MovieDetailScreen : Screen("", Icons.Default.List, "movie_detail_screen")
    object FavoriteMovieScreen :
        Screen("Favorites", Icons.Default.Favorite, "favorite_movie_screen")

    object SearchMovie : Screen("Search", Icons.Default.Search, "search_movie")
}