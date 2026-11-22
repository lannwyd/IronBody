package com.ironlog.gym.ui.screens.exercises.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.gym.data.library.ExerciseLibrary
import com.ironlog.gym.domain.model.Exercise
import com.ironlog.gym.domain.model.LibraryExerciseItem
import com.ironlog.gym.domain.model.MuscleSection
import com.ironlog.gym.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseLibraryUiState(
    val searchQuery: String = "",
    val selectedSection: MuscleSection? = null,
    val filteredItems: List<LibraryExerciseItem> = ExerciseLibrary.all,
    val pendingItem: LibraryExerciseItem? = null,
    val sheetWeight: String = "",
    val sheetUnit: String = "kg",
    val sheetNote: String = "",
    val addSuccess: Boolean = false
)

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseLibraryUiState())
    val uiState: StateFlow<ExerciseLibraryUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refilter()
    }

    fun onSectionSelected(section: MuscleSection?) {
        _uiState.update { it.copy(selectedSection = section) }
        refilter()
    }

    private fun refilter() {
        val s = _uiState.value
        _uiState.update {
            it.copy(filteredItems = ExerciseLibrary.filteredBy(s.selectedSection, s.searchQuery))
        }
    }

    fun onExerciseTapped(item: LibraryExerciseItem) {
        _uiState.update { it.copy(pendingItem = item, sheetWeight = "", sheetUnit = "kg", sheetNote = "", addSuccess = false) }
    }

    fun onDismissSheet() {
        _uiState.update { it.copy(pendingItem = null, addSuccess = false) }
    }

    fun onWeightChange(w: String) { _uiState.update { it.copy(sheetWeight = w) } }
    fun onUnitChange(u: String)   { _uiState.update { it.copy(sheetUnit = u) }   }
    fun onNoteChange(n: String)   { _uiState.update { it.copy(sheetNote = n) }   }

    fun onConfirmAdd() {
        val state = _uiState.value
        val item = state.pendingItem ?: return
        val weight = state.sheetWeight.toFloatOrNull() ?: 0f
        viewModelScope.launch {
            exerciseRepository.addExercise(
                Exercise(
                    name = item.name,
                    weight = weight,
                    unit = state.sheetUnit,
                    imagePath = null,
                    sortOrder = 999,
                    createdAt = System.currentTimeMillis(),
                    note = state.sheetNote,
                    section = item.section.id
                )
            )
            _uiState.update { it.copy(addSuccess = true, pendingItem = null) }
        }
    }

    fun clearSuccess() { _uiState.update { it.copy(addSuccess = false) } }
}
