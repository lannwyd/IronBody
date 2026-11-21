package com.ironlog.gym.ui.screens.splits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironlog.gym.R
import com.ironlog.gym.domain.model.FamousSplitPreset
import com.ironlog.gym.domain.model.GymSplit
import com.ironlog.gym.ui.components.IronLogTopAppBar
import com.ironlog.gym.ui.components.pressScale
import com.ironlog.gym.ui.screens.splits.components.AddEditDayDialog
import com.ironlog.gym.ui.screens.splits.components.EditSplitDialog
import com.ironlog.gym.ui.screens.splits.components.FamousSplitsDialog
import com.ironlog.gym.ui.screens.splits.components.SplitDayCard
import com.ironlog.gym.ui.screens.splits.components.SplitSelectorBottomSheet
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun SplitsScreen(
    onNavigateToDay: (dayId: Long, splitId: Long, dayName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplitsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            IronLogTopAppBar(
                title = stringResource(R.string.splits_screen_title),
                showLogo = true,
                actions = {
                    // Button to open famous presets
                    IconButton(
                        onClick = { viewModel.showFamousSplitsDialog() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.splits_famous_presets),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Button to switch or create splits
                    IconButton(
                        onClick = { viewModel.showSplitSelectorSheet() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = stringResource(R.string.splits_choose_split),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.activeSplit != null) {
                FloatingActionButton(
                    onClick = { viewModel.showAddDayDialog() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.pressScale()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.splits_add_day)
                    )
                }
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
            } else if (uiState.activeSplit == null) {
                // Empty state if no splits exist
                SplitsEmptyState(
                    onChooseFamous = { viewModel.showFamousSplitsDialog() },
                    onCreateCustom = { viewModel.showNewSplitDialog() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val lazyListState = rememberLazyListState()
                val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    // Offset by header items: header item is at index 0, so days start at index 1
                    val fromDayIndex = from.index - 1
                    val toDayIndex = to.index - 1
                    if (fromDayIndex >= 0 && toDayIndex >= 0) {
                        viewModel.onReorderDays(fromDayIndex, toDayIndex)
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header: Active Split Details Card
                    item(key = "active_split_header") {
                        ActiveSplitHeroCard(
                            split = uiState.activeSplit!!,
                            dayCount = uiState.days.size,
                            totalExercises = uiState.days.sumOf { it.exerciseCount },
                            onRenameClick = { viewModel.showRenameSplitDialog() },
                            onFamousClick = { viewModel.showFamousSplitsDialog() },
                            onSwitchClick = { viewModel.showSplitSelectorSheet() }
                        )
                    }

                    // Section Title: Split Days
                    item(key = "days_section_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 4.dp, start = 4.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.splits_days_title).uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "${uiState.days.size} ${if (uiState.days.size == 1) "day" else "days"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (uiState.days.isEmpty()) {
                        item(key = "empty_days") {
                            EmptyDaysCard(onAddDay = { viewModel.showAddDayDialog() })
                        }
                    } else {
                        items(
                            items = uiState.days,
                            key = { it.id }
                        ) { day ->
                            val dayIndex = uiState.days.indexOf(day)
                            ReorderableItem(
                                state = reorderableLazyColumnState,
                                key = day.id
                            ) { isDragging ->
                                SplitDayCard(
                                    day = day,
                                    dayIndex = dayIndex,
                                    onClick = {
                                        onNavigateToDay(day.id, day.splitId, day.name)
                                    },
                                    onRenameDay = {
                                        viewModel.showEditDayDialog(day)
                                    },
                                    onDeleteDay = {
                                        viewModel.showDeleteDayDialog(day)
                                    },
                                    dragModifier = Modifier.draggableHandle(),
                                    isDragging = isDragging
                                )
                            }
                        }
                    }

                    // Quick "+ Add Day" action card at bottom of days
                    item(key = "add_day_button_row") {
                        OutlinedButton(
                            onClick = { viewModel.showAddDayDialog() },
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
                                text = stringResource(R.string.splits_add_day),
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

    // Dialogs
    if (uiState.showFamousSplitsDialog) {
        FamousSplitsDialog(
            onSelectPreset = { preset ->
                viewModel.onCreateSplitFromPreset(preset)
            },
            onDismiss = { viewModel.hideFamousSplitsDialog() }
        )
    }

    if (uiState.showRenameSplitDialog && uiState.activeSplit != null) {
        EditSplitDialog(
            initialName = uiState.activeSplit!!.name,
            onConfirm = { newName ->
                viewModel.onRenameSplit(newName)
            },
            onDismiss = { viewModel.hideRenameSplitDialog() }
        )
    }

    if (uiState.showNewSplitDialog) {
        EditSplitDialog(
            initialName = "",
            onConfirm = { name ->
                viewModel.onCreateCustomSplit(name)
            },
            onDismiss = { viewModel.hideNewSplitDialog() }
        )
    }

    if (uiState.showAddDayDialog) {
        AddEditDayDialog(
            initialName = "",
            isEditing = false,
            onConfirm = { name ->
                viewModel.onAddDay(name)
            },
            onDismiss = { viewModel.hideAddDayDialog() }
        )
    }

    val editingDay = uiState.editingDay
    if (editingDay != null) {
        AddEditDayDialog(
            initialName = editingDay.name,
            isEditing = true,
            onConfirm = { newName ->
                viewModel.onRenameDay(editingDay.id, newName)
            },
            onDismiss = { viewModel.hideEditDayDialog() }
        )
    }

    val dayToDelete = uiState.dayToDelete
    if (dayToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDayDialog() },
            title = {
                Text(
                    text = stringResource(R.string.splits_delete_day),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.splits_delete_day_confirm),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onDeleteDay(dayToDelete.id) }
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDayDialog() }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.extraLarge
        )
    }

    val splitToDelete = uiState.splitToDelete
    if (splitToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteSplitDialog() },
            title = {
                Text(
                    text = stringResource(R.string.splits_delete_split),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.splits_delete_split_confirm),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onDeleteSplit(splitToDelete) }
                ) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteSplitDialog() }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.extraLarge
        )
    }

    if (uiState.showSplitSelectorSheet) {
        SplitSelectorBottomSheet(
            splits = uiState.splits,
            activeSplitId = uiState.activeSplit?.id,
            onSelectSplit = { viewModel.onSelectSplit(it) },
            onCreateNewSplit = { viewModel.showNewSplitDialog() },
            onOpenFamousSplits = { viewModel.showFamousSplitsDialog() },
            onDeleteSplit = { viewModel.showDeleteSplitDialog(it) },
            onDismiss = { viewModel.hideSplitSelectorSheet() }
        )
    }
}

@Composable
private fun ActiveSplitHeroCard(
    split: GymSplit,
    dayCount: Int,
    totalExercises: Int,
    onRenameClick: () -> Unit,
    onFamousClick: () -> Unit,
    onSwitchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Active Tag & Switch Split Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.splits_active_badge),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(onClick = onSwitchClick)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.splits_choose_split),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Split Name & Quick Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = split.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(
                    onClick = onRenameClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.splits_rename_split),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (split.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = split.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats and Quick Template Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$dayCount Days",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$totalExercises Exercises",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Famous Split Template Selector chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(onClick = onFamousClick)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.splits_famous_presets),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDaysCard(
    onAddDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.splits_empty_days_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.splits_empty_days_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onAddDay,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(stringResource(R.string.splits_add_day))
            }
        }
    }
}

@Composable
private fun SplitsEmptyState(
    onChooseFamous: () -> Unit,
    onCreateCustom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.splits_no_splits_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.splits_no_splits_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onChooseFamous,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.splits_famous_presets),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = onCreateCustom,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(
                text = stringResource(R.string.splits_new_split),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
