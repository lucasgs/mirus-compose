package com.dendron.mirus.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dendron.mirus.presentation.ui.theme.MyPurple700

@Composable
fun MainBottomNavigation(navController: NavHostController) {
    val items = listOf(
        Screen.MovieListScreen,
        Screen.SearchMovie,
        Screen.FavoriteMovieScreen,
    )
    NavigationBar(
        containerColor = MyPurple700,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            val isCurrentRoute = currentRoute == item.route
            NavigationBarItem(icon = {
                Icon(
                    item.icon,
                    contentDescription = item.title
                )
            },
                alwaysShowLabel = true,
                label = {
                    Text(
                        item.title,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize
                    )
                },
                selected = isCurrentRoute,
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Color.White,
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Gray,
                ),
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}