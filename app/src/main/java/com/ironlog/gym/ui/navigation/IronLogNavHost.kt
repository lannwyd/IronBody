package com.ironlog.gym.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ironlog.gym.ui.screens.bodyweight.BodyWeightScreen
import com.ironlog.gym.ui.screens.exerciseEdit.ExerciseEditScreen
import com.ironlog.gym.ui.screens.exercises.ExerciseListScreen

@Composable
fun IronLogNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ExerciseList,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        }
    ) {
        composable<Screen.ExerciseList> {
            ExerciseListScreen(
                onNavigateToAddExercise = { initialSection ->
                    navController.navigate(Screen.ExerciseEdit(initialSection = initialSection))
                },
                onNavigateToEditExercise = { exerciseId ->
                    navController.navigate(Screen.ExerciseEdit(exerciseId = exerciseId))
                },
                onNavigateToBodyWeight = {
                    navController.navigate(Screen.BodyWeight)
                }
            )
        }

        composable<Screen.ExerciseEdit> {
            ExerciseEditScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.BodyWeight> {
            BodyWeightScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
