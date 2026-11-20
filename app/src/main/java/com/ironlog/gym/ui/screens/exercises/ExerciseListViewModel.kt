package com.ironlog.gym.ui.screens.exercises

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.gym.domain.model.Exercise
import com.ironlog.gym.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchQuery = savedStateHandle.getStateFlow("search_query", "")
    private val _selectedSection = savedStateHandle.getStateFlow<String?>("selected_section", null)
    private val _selectedUnitFilter = savedStateHandle.getStateFlow<String?>("unit_filter", null)
    private val _recentlyDeleted = MutableStateFlow<Exercise?>(null)

    val uiState: StateFlow<ExerciseListUiState> = combine(
        exerciseRepository.getExercises(),
        _searchQuery,
        _selectedSection,
        _selectedUnitFilter,
        _recentlyDeleted
    ) { exercises, query, section, unitFilter, recentlyDeleted ->
        val counts = exercises.groupingBy { it.section.lowercase() }.eachCount()

        val filtered = exercises.filter { exercise ->
            val matchesQuery = query.isBlank() ||
                    exercise.name.contains(query, ignoreCase = true) ||
                    exercise.note.contains(query, ignoreCase = true)
            val matchesUnit = unitFilter == null || exercise.unit.equals(unitFilter, ignoreCase = true)
            val matchesSection = section == null || exercise.section.equals(section, ignoreCase = true)
            matchesQuery && matchesUnit && matchesSection
        }

        ExerciseListUiState(
            exercises = exercises,
            filteredExercises = filtered,
            searchQuery = query,
            selectedSection = section,
            selectedUnitFilter = unitFilter,
            sectionCounts = counts,
            isLoading = false,
            recentlyDeletedExercise = recentlyDeleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExerciseListUiState()
    )

    fun onSearchQueryChange(query: String) {
        savedStateHandle["search_query"] = query
    }

    fun onSectionSelected(section: String?) {
        savedStateHandle["selected_section"] = section
    }

    fun onUnitFilterSelected(unit: String?) {
        savedStateHandle["unit_filter"] = unit
    }

    fun onIncreaseWeight(exercise: Exercise) {
        viewModelScope.launch {
            val step = 1.0f
            val newWeight = (exercise.weight + step)
            val rounded = (Math.round(newWeight * 10.0) / 10.0).toFloat()
            exerciseRepository.updateWeight(exercise.id, rounded)
        }
    }

    fun onDecreaseWeight(exercise: Exercise) {
        viewModelScope.launch {
            val step = 1.0f
            val newWeight = max(0f, exercise.weight - step)
            val rounded = (Math.round(newWeight * 10.0) / 10.0).toFloat()
            exerciseRepository.updateWeight(exercise.id, rounded)
        }
    }

    fun onReorder(fromIndex: Int, toIndex: Int) {
        val currentList = uiState.value.filteredExercises.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            viewModelScope.launch {
                val orderedIds = currentList.map { it.id }
                exerciseRepository.updateSortOrders(orderedIds)
            }
        }
    }

    fun onDeleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            _recentlyDeleted.value = exercise
            exerciseRepository.deleteExercise(exercise, deleteImageFile = false)
        }
    }

    fun onUndoDelete() {
        val exerciseToRestore = _recentlyDeleted.value ?: return
        viewModelScope.launch {
            exerciseRepository.restoreExercise(exerciseToRestore)
            _recentlyDeleted.value = null
        }
    }

    fun onDismissSnackbar() {
        val exercise = _recentlyDeleted.value
        _recentlyDeleted.value = null
        if (exercise != null && !exercise.imagePath.isNullOrBlank()) {
            viewModelScope.launch {
                exerciseRepository.deleteExercise(exercise, deleteImageFile = true)
            }
        }
    }
}
