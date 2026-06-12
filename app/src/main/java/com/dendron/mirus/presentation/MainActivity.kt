package com.dendron.mirus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dendron.mirus.ui.navigation.MainBottomNavigation
import com.dendron.mirus.ui.navigation.NavigationGraph
import com.dendron.mirus.ui.navigation.Screen
import com.dendron.mirus.ui.theme.MirusTheme
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

@Composable
fun MirusApp(
    windowSize: WindowSizeClass,
) {
    val navController = rememberNavController()

    val contentType: ContentType = when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> ContentType.DUAL_PANE
        else -> ContentType.SINGLE_PANE
    }

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val showBottomBar =
        navBackStackEntry?.destination?.hierarchy?.any { it.route in Screen.topLevelRoutes } == true &&
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