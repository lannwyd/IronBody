package com.ironlog.gym.ui.screens.splits

import com.ironlog.gym.domain.model.GymSplit
import com.ironlog.gym.domain.model.SplitDay

data class SplitsUiState(
    val splits: List<GymSplit> = emptyList(),
    val activeSplit: GymSplit? = null,
    val days: List<SplitDay> = emptyList(),
    val isLoading: Boolean = true,
    val showFamousSplitsDialog: Boolean = false,
    val showNewSplitDialog: Boolean = false,
    val showRenameSplitDialog: Boolean = false,
    val showAddDayDialog: Boolean = false,
    val editingDay: SplitDay? = null,
    val dayToDelete: SplitDay? = null,
    val splitToDelete: GymSplit? = null,
    val showSplitSelectorSheet: Boolean = false,
    val showShareDialog: Boolean = false,
    val shareText: String = "",
    val showImportDialog: Boolean = false,
    val snackbarMessage: String? = null
)
