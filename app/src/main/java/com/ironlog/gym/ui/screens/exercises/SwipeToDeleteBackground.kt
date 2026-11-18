package com.ironlog.gym.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ironlog.gym.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteBackground(
    dismissState: SwipeToDismissBoxState,
    modifier: Modifier = Modifier
) {
    val isDismissedToEnd = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd
    val alignment = if (isDismissedToEnd) Alignment.CenterStart else Alignment.CenterEnd
    val padding = if (isDismissedToEnd) Modifier.padding(start = 24.dp) else Modifier.padding(end = 24.dp)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = stringResource(R.string.action_delete),
            tint = MaterialTheme.colorScheme.error,
            modifier = padding.size(28.dp)
        )
    }
}
