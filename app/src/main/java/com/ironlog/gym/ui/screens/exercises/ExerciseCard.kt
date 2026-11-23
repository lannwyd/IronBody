package com.ironlog.gym.ui.screens.exercises

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ironlog.gym.R
import com.ironlog.gym.domain.model.Exercise
import com.ironlog.gym.domain.model.MuscleSection
import com.ironlog.gym.ui.components.pressScale
import java.io.File

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit,
    onIncreaseWeight: () -> Unit,
    onDecreaseWeight: () -> Unit,
    dragModifier: Modifier = Modifier,
    isDragging: Boolean = false,
    modifier: Modifier = Modifier
) {
    val elevation = if (isDragging) 12.dp else 0.dp
    val cardBorder = if (isDragging) {
        BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise Picture or Fallback Barbell Icon
            ExerciseThumbnail(
                imagePath = exercise.imagePath,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Exercise Title & Note
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Section Tag
                val section = MuscleSection.fromId(exercise.section)
                Text(
                    text = section.displayName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (exercise.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = exercise.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick +/- Weight Change Controls
                QuickWeightControls(
                    weight = exercise.weight,
                    unit = exercise.unit,
                    onIncrease = onIncreaseWeight,
                    onDecrease = onDecreaseWeight
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Drag handle icon for Calvin Reorderable
            Box(
                modifier = dragModifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = stringResource(R.string.action_drag_to_reorder),
                    tint = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ExerciseThumbnail(
    imagePath: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageModel: Any? = when {
        imagePath.isNullOrBlank() -> null
        File(imagePath).exists() -> File(imagePath)
        imagePath.startsWith("android.resource://") -> android.net.Uri.parse(imagePath)
        else -> {
            val resId = context.resources.getIdentifier(imagePath, "drawable", context.packageName)
            if (resId != 0) resId else null
        }
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (imageModel != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.cd_exercise_image),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Sleek Gym Dumbbell Placeholder
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = stringResource(R.string.cd_exercise_image),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
