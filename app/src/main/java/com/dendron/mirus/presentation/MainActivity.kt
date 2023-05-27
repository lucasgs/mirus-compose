package com.dendron.mirus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dendron.mirus.presentation.navigation.MainBottomNavigation
import com.dendron.mirus.presentation.navigation.NavigationGraph
import com.dendron.mirus.presentation.navigation.Screen
import com.dendron.mirus.presentation.ui.theme.MirusTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    MirusTheme {
        val navController = rememberAnimatedNavController()
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