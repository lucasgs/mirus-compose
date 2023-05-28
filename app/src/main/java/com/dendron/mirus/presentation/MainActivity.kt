package com.dendron.mirus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dendron.mirus.presentation.navigation.MainBottomNavigation
import com.dendron.mirus.presentation.navigation.NavigationGraph
import com.dendron.mirus.presentation.navigation.Screen
import com.dendron.mirus.presentation.ui.theme.MirusTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MirusTheme {
                val windowSize = calculateWindowSizeClass(this)
                MirusApp(windowSize)
            }
        }
    }
}

enum class ContentType {
    SINGLE_PANE, DUAL_PANE
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MirusApp(
    windowSize: WindowSizeClass,
) {
    val navController = rememberAnimatedNavController()

    val contentType: ContentType = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> ContentType.DUAL_PANE
        else -> ContentType.SINGLE_PANE
    }

    val screens = listOf(Screen.MovieListScreen, Screen.FavoriteMovieScreen, Screen.SearchMovie)
    val showBottomBar =
        navController.currentBackStackEntryAsState().value?.destination?.route in screens.map { it.route } &&
                contentType == ContentType.SINGLE_PANE

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        if (showBottomBar) {
            MainBottomNavigation(navController = navController)
        }
    }) { innerPadding ->
        NavigationGraph(
            navController = navController, paddingValues = innerPadding, contentType = contentType
        )
    }
}