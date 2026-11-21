package com.ironlog.gym

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ironlog.gym.ui.components.IronLogBottomBar
import com.ironlog.gym.ui.navigation.IronLogNavHost
import com.ironlog.gym.ui.navigation.Screen
import com.ironlog.gym.ui.theme.IronLogTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IronLogTheme {
                MainApp(intent = intent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun MainApp(
    intent: Intent?,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show bottom bar on primary top-level tabs
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute(Screen.ExerciseList::class) ||
        it.hasRoute(Screen.Splits::class) ||
        it.hasRoute(Screen.BodyWeight::class) ||
        it.hasRoute(Screen.Timer::class)
    } == true

    // Handle notification click intent to jump straight to Timer
    LaunchedEffect(intent) {
        if (intent?.getStringExtra("navigate_to") == "timer") {
            navController.navigate(Screen.Timer) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                IronLogBottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        IronLogNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
