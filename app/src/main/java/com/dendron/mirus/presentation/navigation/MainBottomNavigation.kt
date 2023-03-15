package com.dendron.mirus.presentation.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dendron.mirus.presentation.ui.theme.MyPurple200

@Composable
fun MainBottomNavigation(navController: NavHostController) {
    val items = listOf(
        Screen.MovieListScreen,
        Screen.SearchMovie,
        Screen.FavoriteMovieScreen,
    )
    BottomNavigation(
        backgroundColor = MyPurple200,
        contentColor = Color.White,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            val isCurrentRoute = currentRoute == item.route
            BottomNavigationItem(icon = {
                Icon(
                    item.icon,
                    contentDescription = item.title
                )
            },
                alwaysShowLabel = true,
                label = { Text(item.title, fontSize = 9.sp) },
                selected = isCurrentRoute,
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Gray,
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