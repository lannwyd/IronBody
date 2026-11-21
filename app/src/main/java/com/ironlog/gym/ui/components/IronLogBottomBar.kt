package com.ironlog.gym.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.ironlog.gym.R
import com.ironlog.gym.ui.navigation.Screen

sealed class BottomNavItem(
    val route: Screen,
    val titleRes: Int,
    val icon: ImageVector
) {
    data object Exercises : BottomNavItem(
        route = Screen.ExerciseList,
        titleRes = R.string.nav_exercises,
        icon = Icons.Default.FitnessCenter
    )

    data object Splits : BottomNavItem(
        route = Screen.Splits,
        titleRes = R.string.nav_splits,
        icon = Icons.Default.CalendarMonth
    )

    data object Session : BottomNavItem(
        route = Screen.Session(),
        titleRes = R.string.nav_session,
        icon = Icons.Default.PlayCircle
    )

    data object BodyWeight : BottomNavItem(
        route = Screen.BodyWeight,
        titleRes = R.string.nav_body_weight,
        icon = Icons.Default.MonitorWeight
    )

    data object Timer : BottomNavItem(
        route = Screen.Timer,
        titleRes = R.string.nav_timer,
        icon = Icons.Default.Timer
    )

    companion object {
        val items = listOf(Exercises, Splits, Session, BodyWeight, Timer)
    }
}

@Composable
fun IronLogBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.height(68.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        BottomNavItem.items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.route::class)
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.titleRes),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
