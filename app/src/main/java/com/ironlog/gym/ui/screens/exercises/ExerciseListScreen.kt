package com.ironlog.gym.ui.screens.exercises

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironlog.gym.R
import com.ironlog.gym.domain.model.MuscleSection
import com.ironlog.gym.ui.components.ExerciseEmptyState
import com.ironlog.gym.ui.components.IronLogTopAppBar
import com.ironlog.gym.ui.components.SearchAndFilterBar
import com.ironlog.gym.ui.components.pressScale
import com.ironlog.gym.ui.theme.LocalThemeController
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    onNavigateToAddExercise: (String?) -> Unit,
    onNavigateToEditExercise: (Long) -> Unit,
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Observe deletion to trigger Undo Snackbar
    LaunchedEffect(uiState.recentlyDeletedExercise) {
        val deleted = uiState.recentlyDeletedExercise
        if (deleted != null) {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.exercise_deleted),
                actionLabel = context.getString(R.string.action_undo),
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> viewModel.onUndoDelete()
                SnackbarResult.Dismissed -> viewModel.onDismissSnackbar()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            val themeController = LocalThemeController.current
            IronLogTopAppBar(
                title = stringResource(R.string.app_name),
                showLogo = true,
                navigationIcon = {
                    Row(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .clickable(onClick = onNavigateToLibrary)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                            contentDescription = "Exercises list",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Exercises list",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = themeController.onToggle,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (themeController.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (themeController.isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddExercise(uiState.selectedSection) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.pressScale()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add_exercise)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search, Section chips, and filter controls
            SearchAndFilterBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                selectedSection = uiState.selectedSection,
                onSectionSelected = viewModel::onSectionSelected,
                selectedUnitFilter = uiState.selectedUnitFilter,
                onUnitFilterSelected = viewModel::onUnitFilterSelected,
                sectionCounts = uiState.sectionCounts
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (uiState.filteredExercises.isEmpty() && !uiState.isLoading) {
                    ExerciseEmptyState(
                        isSearching = uiState.searchQuery.isNotEmpty() || uiState.selectedUnitFilter != null,
                        onAddExerciseClick = { onNavigateToAddExercise(uiState.selectedSection) },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val lazyListState = rememberLazyListState()
                    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
                        viewModel.onReorder(from.index, to.index)
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // If "All" is selected and not searching, show organized section headers
                        if (uiState.selectedSection == null && uiState.searchQuery.isBlank()) {
                            val grouped = uiState.filteredExercises.groupBy { it.section.lowercase() }
                            MuscleSection.ALL.forEach { section ->
                                val sectionItems = grouped[section.id] ?: emptyList()
                                if (sectionItems.isNotEmpty()) {
                                    item(key = "section_header_${section.id}") {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp, bottom = 4.dp, start = 4.dp, end = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = section.displayName.uppercase(),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary,
                                                letterSpacing = 1.2.sp
                                            )
                                            Text(
                                                text = "${sectionItems.size} ${if (sectionItems.size == 1) "exercise" else "exercises"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    items(
                                        items = sectionItems,
                                        key = { it.id }
                                    ) { exercise ->
                                        val dismissState = rememberSwipeToDismissBoxState(
                                            confirmValueChange = { dismissValue ->
                                                if (dismissValue == SwipeToDismissBoxValue.EndToStart ||
                                                    dismissValue == SwipeToDismissBoxValue.StartToEnd
                                                ) {
                                                    viewModel.onDeleteExercise(exercise)
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                        )

                                        SwipeToDismissBox(
                                            state = dismissState,
                                            backgroundContent = {
                                                SwipeToDeleteBackground(dismissState = dismissState)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            ExerciseCard(
                                                exercise = exercise,
                                                onClick = { onNavigateToEditExercise(exercise.id) },
                                                onIncreaseWeight = { viewModel.onIncreaseWeight(exercise) },
                                                onDecreaseWeight = { viewModel.onDecreaseWeight(exercise) }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Filtered by a single section or by search query (supports reordering)
                            items(
                                items = uiState.filteredExercises,
                                key = { it.id }
                            ) { exercise ->
                                ReorderableItem(
                                    state = reorderableLazyColumnState,
                                    key = exercise.id
                                ) { isDragging ->
                                    val dismissState = rememberSwipeToDismissBoxState(
                                        confirmValueChange = { dismissValue ->
                                            if (dismissValue == SwipeToDismissBoxValue.EndToStart ||
                                                dismissValue == SwipeToDismissBoxValue.StartToEnd
                                            ) {
                                                viewModel.onDeleteExercise(exercise)
                                                true
                                            } else {
                                                false
                                            }
                                        }
                                    )

                                    SwipeToDismissBox(
                                        state = dismissState,
                                        backgroundContent = {
                                            SwipeToDeleteBackground(dismissState = dismissState)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem()
                                    ) {
                                        ExerciseCard(
                                            exercise = exercise,
                                            onClick = { onNavigateToEditExercise(exercise.id) },
                                            onIncreaseWeight = { viewModel.onIncreaseWeight(exercise) },
                                            onDecreaseWeight = { viewModel.onDecreaseWeight(exercise) },
                                            dragModifier = Modifier.draggableHandle(),
                                            isDragging = isDragging
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
