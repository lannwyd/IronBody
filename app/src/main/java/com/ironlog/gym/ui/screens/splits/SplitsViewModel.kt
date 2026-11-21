package com.ironlog.gym.ui.screens.splits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.gym.domain.model.FamousSplitPreset
import com.ironlog.gym.domain.model.GymSplit
import com.ironlog.gym.domain.model.SharedSplit
import com.ironlog.gym.domain.model.SplitDay
import com.ironlog.gym.domain.repository.SplitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SplitsViewModel @Inject constructor(
    private val splitRepository: SplitRepository
) : ViewModel() {

    private val _dialogState = MutableStateFlow(DialogState())

    private data class DialogState(
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

    init {
        viewModelScope.launch {
            splitRepository.ensureDefaultSplitExists()
        }
    }

    val uiState: StateFlow<SplitsUiState> = splitRepository.getActiveSplit()
        .flatMapLatest { activeSplit ->
            val allSplitsFlow = splitRepository.getAllSplits()
            val daysFlow = if (activeSplit != null) {
                splitRepository.getDaysForSplit(activeSplit.id)
            } else {
                flowOf(emptyList())
            }

            combine(
                allSplitsFlow,
                daysFlow,
                _dialogState
            ) { allSplits, days, dialogs ->
                SplitsUiState(
                    splits = allSplits,
                    activeSplit = activeSplit,
                    days = days,
                    isLoading = false,
                    showFamousSplitsDialog = dialogs.showFamousSplitsDialog,
                    showNewSplitDialog = dialogs.showNewSplitDialog,
                    showRenameSplitDialog = dialogs.showRenameSplitDialog,
                    showAddDayDialog = dialogs.showAddDayDialog,
                    editingDay = dialogs.editingDay,
                    dayToDelete = dialogs.dayToDelete,
                    splitToDelete = dialogs.splitToDelete,
                    showSplitSelectorSheet = dialogs.showSplitSelectorSheet,
                    showShareDialog = dialogs.showShareDialog,
                    shareText = dialogs.shareText,
                    showImportDialog = dialogs.showImportDialog,
                    snackbarMessage = dialogs.snackbarMessage
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SplitsUiState()
        )

    fun onSelectSplit(splitId: Long) {
        viewModelScope.launch {
            splitRepository.setActiveSplit(splitId)
        }
    }

    fun onCreateCustomSplit(name: String, days: List<String> = emptyList()) {
        viewModelScope.launch {
            val defaultDays = if (days.isEmpty()) listOf("Day 1 - Push", "Day 2 - Pull", "Day 3 - Legs") else days
            splitRepository.createSplit(name = name, days = defaultDays)
            hideNewSplitDialog()
        }
    }

    fun onCreateSplitFromPreset(preset: FamousSplitPreset) {
        viewModelScope.launch {
            splitRepository.createSplitFromPreset(preset)
            hideFamousSplitsDialog()
        }
    }

    fun onRenameSplit(newName: String) {
        val activeSplit = uiState.value.activeSplit ?: return
        viewModelScope.launch {
            splitRepository.updateSplitName(activeSplit.id, newName)
            hideRenameSplitDialog()
        }
    }

    fun onDeleteSplit(split: GymSplit) {
        viewModelScope.launch {
            splitRepository.deleteSplit(split.id)
            hideDeleteSplitDialog()
        }
    }

    fun onAddDay(dayName: String) {
        val activeSplit = uiState.value.activeSplit ?: return
        viewModelScope.launch {
            splitRepository.addDay(activeSplit.id, dayName)
            hideAddDayDialog()
        }
    }

    fun onRenameDay(dayId: Long, newName: String) {
        viewModelScope.launch {
            splitRepository.updateDayName(dayId, newName)
            hideEditDayDialog()
        }
    }

    fun onDeleteDay(dayId: Long) {
        viewModelScope.launch {
            splitRepository.deleteDay(dayId)
            hideDeleteDayDialog()
        }
    }

    fun onReorderDays(fromIndex: Int, toIndex: Int) {
        val currentList = uiState.value.days.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            viewModelScope.launch {
                val orderedIds = currentList.map { it.id }
                splitRepository.updateDaySortOrders(orderedIds)
            }
        }
    }

    // === Sharing & Importing ===
    fun onShareSplit(targetSplitId: Long? = null) {
        val splitId = targetSplitId ?: uiState.value.activeSplit?.id ?: return
        viewModelScope.launch {
            val shareText = splitRepository.exportSplitToShareText(splitId)
            if (!shareText.isNullOrBlank()) {
                _dialogState.value = _dialogState.value.copy(
                    showShareDialog = true,
                    shareText = shareText
                )
            }
        }
    }

    fun parseSplitFromText(rawText: String): SharedSplit? {
        return splitRepository.parseSplitFromText(rawText)
    }

    fun onImportSplit(sharedSplit: SharedSplit) {
        viewModelScope.launch {
            val newSplitId = splitRepository.importSplit(sharedSplit)
            _dialogState.value = _dialogState.value.copy(
                showImportDialog = false,
                snackbarMessage = "Split '${sharedSplit.name}' imported with all exercises!"
            )
        }
    }

    fun clearSnackbarMessage() {
        _dialogState.value = _dialogState.value.copy(snackbarMessage = null)
    }

    fun hideShareDialog() {
        _dialogState.value = _dialogState.value.copy(showShareDialog = false)
    }

    fun showImportDialog() {
        _dialogState.value = _dialogState.value.copy(showImportDialog = true)
    }

    fun hideImportDialog() {
        _dialogState.value = _dialogState.value.copy(showImportDialog = false)
    }

    fun showFamousSplitsDialog() { _dialogState.value = _dialogState.value.copy(showFamousSplitsDialog = true) }
    fun hideFamousSplitsDialog() { _dialogState.value = _dialogState.value.copy(showFamousSplitsDialog = false) }

    fun showNewSplitDialog() { _dialogState.value = _dialogState.value.copy(showNewSplitDialog = true) }
    fun hideNewSplitDialog() { _dialogState.value = _dialogState.value.copy(showNewSplitDialog = false) }

    fun showRenameSplitDialog() { _dialogState.value = _dialogState.value.copy(showRenameSplitDialog = true) }
    fun hideRenameSplitDialog() { _dialogState.value = _dialogState.value.copy(showRenameSplitDialog = false) }

    fun showAddDayDialog() { _dialogState.value = _dialogState.value.copy(showAddDayDialog = true) }
    fun hideAddDayDialog() { _dialogState.value = _dialogState.value.copy(showAddDayDialog = false) }

    fun showEditDayDialog(day: SplitDay) { _dialogState.value = _dialogState.value.copy(editingDay = day) }
    fun hideEditDayDialog() { _dialogState.value = _dialogState.value.copy(editingDay = null) }

    fun showDeleteDayDialog(day: SplitDay) { _dialogState.value = _dialogState.value.copy(dayToDelete = day) }
    fun hideDeleteDayDialog() { _dialogState.value = _dialogState.value.copy(dayToDelete = null) }

    fun showDeleteSplitDialog(split: GymSplit) { _dialogState.value = _dialogState.value.copy(splitToDelete = split) }
    fun hideDeleteSplitDialog() { _dialogState.value = _dialogState.value.copy(splitToDelete = null) }

    fun showSplitSelectorSheet() { _dialogState.value = _dialogState.value.copy(showSplitSelectorSheet = true) }
    fun hideSplitSelectorSheet() { _dialogState.value = _dialogState.value.copy(showSplitSelectorSheet = false) }
}
