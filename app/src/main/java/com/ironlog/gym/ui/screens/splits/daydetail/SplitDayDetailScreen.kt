package com.ironlog.gym.ui.screens.splits.daydetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironlog.gym.R
import androidx.compose.material.icons.filled.PlayArrow
import com.ironlog.gym.ui.components.IronLogTopAppBar
import com.ironlog.gym.ui.components.pressScale
import com.ironlog.gym.ui.screens.splits.components.AddEditDayDialog
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun SplitDayDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToExerciseDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplitDayDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.recentlyRemovedExercise) {
        val removed = uiState.recentlyRemovedExercise
        if (removed != null) {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.split_day_exercise_removed),
                actionLabel = context.getString(R.string.action_undo),
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> viewModel.onUndoRemove()
                SnackbarResult.Dismissed -> viewModel.onDismissSnackbar()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            IronLogTopAppBar(
                title = uiState.dayName,
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { viewModel.showRenameDayDialog() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.splits_rename_day),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.showExercisePicker() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.split_day_add_exercises),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showExercisePicker() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.pressScale()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.split_day_add_exercises)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.dayExercises.isEmpty()) {
                // Empty state for day with no exercises yet
                EmptyDayExercisesState(
                    onAddExercises = { viewModel.showExercisePicker() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val lazyListState = rememberLazyListState()
                val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    // First item is header at index 0, exercises start at index 1
                    val fromExerciseIndex = from.index - 1
                    val toExerciseIndex = to.index - 1
                    if (fromExerciseIndex >= 0 && toExerciseIndex >= 0) {
                        viewModel.onReorderExercises(fromExerciseIndex, toExerciseIndex)
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header Order Banner
                    item(key = "day_order_banner") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${uiState.dayExercises.size} ${if (uiState.dayExercises.size == 1) "exercise" else "exercises"} in order",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "Drag handle to reorder",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Respected Order Exercises
                    items(
                        items = uiState.dayExercises,
                        key = { it.splitDayExerciseId }
                    ) { dayExercise ->
                        val orderIndex = uiState.dayExercises.indexOf(dayExercise)
                        ReorderableItem(
                            state = reorderableLazyColumnState,
                            key = dayExercise.splitDayExerciseId
                        ) { isDragging ->
                            SplitDayExerciseCard(
                                dayExercise = dayExercise,
                                orderIndex = orderIndex,
                                onClick = { onNavigateToExerciseDetail(dayExercise.exercise.id) },
                                onIncreaseWeight = { viewModel.onIncreaseWeight(dayExercise.exercise) },
                                onDecreaseWeight = { viewModel.onDecreaseWeight(dayExercise.exercise) },
                                onRemoveFromDay = { viewModel.onRemoveExercise(dayExercise) },
                                dragModifier = Modifier.draggableHandle(),
                                isDragging = isDragging
                            )
                        }
                    }

                    // Add More Exercises button row at bottom of list
                    item(key = "add_more_exercises_row") {
                        OutlinedButton(
                            onClick = { viewModel.showExercisePicker() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .pressScale(),
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.split_day_add_exercises),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    // Exercise Picker Sheet
    if (uiState.showExercisePicker) {
        val alreadyAddedIds = remember(uiState.dayExercises) {
            uiState.dayExercises.map { it.exercise.id }.toSet()
        }

        ExercisePickerSheet(
            allExercises = uiState.allGlobalExercises,
            alreadyAddedExerciseIds = alreadyAddedIds,
            onAddSelectedExercises = { selectedIds ->
                viewModel.onAddExercises(selectedIds)
            },
            onDismiss = { viewModel.hideExercisePicker() }
        )
    }

    // Rename Day Dialog
    if (uiState.showRenameDayDialog) {
        AddEditDayDialog(
            initialName = uiState.dayName,
            isEditing = true,
            onConfirm = { newName ->
                viewModel.onRenameDay(newName)
            },
            onDismiss = { viewModel.hideRenameDayDialog() }
        )
    }
}

@Composable
private fun EmptyDayExercisesState(
    onAddExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.split_day_empty_exercises_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.split_day_empty_exercises_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onAddExercises,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .pressScale(),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.split_day_add_exercises),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
