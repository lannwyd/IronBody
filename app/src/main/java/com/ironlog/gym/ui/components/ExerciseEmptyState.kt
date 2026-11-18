package com.ironlog.gym.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ironlog.gym.R

@Composable
fun ExerciseEmptyState(
    isSearching: Boolean,
    onAddExerciseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Gentle breathing loop animation for the empty state barbell
    val infiniteTransition = rememberInfiniteTransition(label = "empty_state_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Custom animated barbell Canvas
        Canvas(modifier = Modifier.size(140.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val barWidth = 90.dp.toPx() * pulseScale
            val barHeight = 8.dp.toPx()

            // Outer soft glow ring
            drawCircle(
                color = primaryColor.copy(alpha = glowAlpha * 0.3f),
                radius = 56.dp.toPx() * pulseScale,
                center = center
            )

            // Inner circle ring
            drawCircle(
                color = outlineColor,
                radius = 48.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Barbell Shaft
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(center.x - barWidth / 2f, center.y - barHeight / 2f),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // Inner Plates
            val innerPlateW = 10.dp.toPx()
            val innerPlateH = 46.dp.toPx()
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(center.x - barWidth / 2f + 16.dp.toPx(), center.y - innerPlateH / 2f),
                size = Size(innerPlateW, innerPlateH),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(center.x + barWidth / 2f - 16.dp.toPx() - innerPlateW, center.y - innerPlateH / 2f),
                size = Size(innerPlateW, innerPlateH),
                cornerRadius = CornerRadius(3.dp.toPx())
            )

            // Outer Plates
            val outerPlateW = 8.dp.toPx()
            val outerPlateH = 34.dp.toPx()
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(center.x - barWidth / 2f + 4.dp.toPx(), center.y - outerPlateH / 2f),
                size = Size(outerPlateW, outerPlateH),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(center.x + barWidth / 2f - 4.dp.toPx() - outerPlateW, center.y - outerPlateH / 2f),
                size = Size(outerPlateW, outerPlateH),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(
                if (isSearching) R.string.empty_search_title else R.string.empty_exercises_title
            ),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                if (isSearching) R.string.empty_search_subtitle else R.string.empty_exercises_subtitle
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (!isSearching) {
            Spacer(modifier = Modifier.height(24.dp))

            IronLogPrimaryButton(
                onClick = onAddExerciseClick,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.action_add_first_exercise),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
