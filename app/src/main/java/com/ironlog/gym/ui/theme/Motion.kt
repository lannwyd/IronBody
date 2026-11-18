package com.ironlog.gym.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class IronMotion(
    val durationShort: Int = 150,
    val durationMedium: Int = 300,
    val durationLong: Int = 500,

    // Springs for gestures and feedback
    val touchSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    val gentleSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    ),
    val snappySpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    ),

    // Tweens for transitions
    val shortTween: AnimationSpec<Float> = tween(
        durationMillis = 150,
        easing = FastOutSlowInEasing
    ),
    val mediumTween: AnimationSpec<Float> = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    ),
    val enterTransition: AnimationSpec<Float> = tween(
        durationMillis = 300,
        easing = LinearOutSlowInEasing
    )
)

val LocalIronMotion = staticCompositionLocalOf { IronMotion() }
