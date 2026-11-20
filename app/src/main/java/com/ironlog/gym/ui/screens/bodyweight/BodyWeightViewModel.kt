package com.ironlog.gym.ui.screens.bodyweight

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.gym.R
import com.ironlog.gym.domain.model.BodyWeight
import com.ironlog.gym.domain.repository.BodyWeightRepository
import com.ironlog.gym.util.ImageStorageManager
import com.ironlog.gym.util.WeightFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BodyWeightViewModel @Inject constructor(
    private val bodyWeightRepository: BodyWeightRepository,
    private val imageStorageManager: ImageStorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyWeightUiState())
    val uiState: StateFlow<BodyWeightUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bodyWeightRepository.getAllEntries().collect { entries ->
                _uiState.update {
                    it.copy(entries = entries, isLoading = false)
                }
            }
        }
    }

    // --- Dialog open / close ---

    fun onAddNewEntry() {
        _uiState.update {
            it.copy(
                showDialog = true,
                editingEntry = null,
                dialogWeight = "",
                dialogWeightError = null,
                dialogUnit = "kg",
                dialogDate = System.currentTimeMillis(),
                dialogReceiptImagePath = null,
                dialogNote = "",
                dialogIsLoading = false
            )
        }
    }

    fun onEditEntry(entry: BodyWeight) {
        _uiState.update {
            it.copy(
                showDialog = true,
                editingEntry = entry,
                dialogWeight = WeightFormatter.format(entry.weight),
                dialogWeightError = null,
                dialogUnit = entry.unit,
                dialogDate = entry.date,
                dialogReceiptImagePath = entry.receiptImagePath,
                dialogNote = entry.note,
                dialogIsLoading = false
            )
        }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showDialog = false) }
    }

    // --- Dialog field changes ---

    fun onDialogWeightChange(weight: String) {
        _uiState.update {
            it.copy(
                dialogWeight = weight,
                dialogWeightError = if (weight.isNotBlank()) null else it.dialogWeightError
            )
        }
    }

    fun onDialogUnitChange(unit: String) {
        _uiState.update { it.copy(dialogUnit = unit) }
    }

    fun onDialogDateChange(date: Long) {
        _uiState.update { it.copy(dialogDate = date) }
    }

    fun onDialogNoteChange(note: String) {
        _uiState.update { it.copy(dialogNote = note) }
    }

    fun onDialogReceiptPicked(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(dialogIsLoading = true) }
            val savedPath = imageStorageManager.saveImageFromUri(uri)
            if (savedPath != null) {
                val oldPath = _uiState.value.dialogReceiptImagePath
                if (!oldPath.isNullOrBlank() && oldPath != savedPath) {
                    imageStorageManager.deleteImageFile(oldPath)
                }
                _uiState.update { it.copy(dialogReceiptImagePath = savedPath, dialogIsLoading = false) }
            } else {
                _uiState.update { it.copy(dialogIsLoading = false) }
            }
        }
    }

    fun onDialogRemoveReceipt() {
        val path = _uiState.value.dialogReceiptImagePath
        if (!path.isNullOrBlank()) {
            viewModelScope.launch { imageStorageManager.deleteImageFile(path) }
        }
        _uiState.update { it.copy(dialogReceiptImagePath = null) }
    }

    // --- Save ---

    fun onSaveEntry() {
        val state = _uiState.value
        val parsed = WeightFormatter.parseOrNull(state.dialogWeight)

        if (state.dialogWeight.isBlank()) {
            _uiState.update { it.copy(dialogWeightError = R.string.error_empty_weight) }
            return
        }
        if (parsed == null) {
            _uiState.update { it.copy(dialogWeightError = R.string.error_invalid_weight) }
            return
        }
        if (parsed < 0f) {
            _uiState.update { it.copy(dialogWeightError = R.string.error_negative_weight) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(dialogIsLoading = true) }

            val editing = state.editingEntry
            if (editing != null) {
                val updated = editing.copy(
                    weight = parsed,
                    unit = state.dialogUnit,
                    date = state.dialogDate,
                    receiptImagePath = state.dialogReceiptImagePath,
                    note = state.dialogNote.trim()
                )
                bodyWeightRepository.updateEntry(updated)
            } else {
                val newEntry = BodyWeight(
                    weight = parsed,
                    unit = state.dialogUnit,
                    date = state.dialogDate,
                    receiptImagePath = state.dialogReceiptImagePath,
                    note = state.dialogNote.trim()
                )
                bodyWeightRepository.addEntry(newEntry)
            }

            _uiState.update { it.copy(showDialog = false, dialogIsLoading = false) }
        }
    }

    // --- Delete ---

    fun onDeleteEntry(entry: BodyWeight) {
        viewModelScope.launch {
            bodyWeightRepository.deleteEntry(entry.id, entry.receiptImagePath)
        }
    }

    // --- Receipt viewer ---

    fun onViewReceipt(path: String?) {
        _uiState.update { it.copy(viewingReceiptPath = path) }
    }

    fun onDismissReceiptViewer() {
        _uiState.update { it.copy(viewingReceiptPath = null) }
    }
}
