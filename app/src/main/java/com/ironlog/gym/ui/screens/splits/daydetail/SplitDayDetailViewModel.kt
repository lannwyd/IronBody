package com.ironlog.gym.ui.screens.splits.daydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ironlog.gym.domain.model.Exercise
import com.ironlog.gym.domain.model.SplitDayExercise
import com.ironlog.gym.domain.repository.ExerciseRepository
import com.ironlog.gym.domain.repository.SplitRepository
import com.ironlog.gym.ui.navigation.Screen
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
class SplitDayDetailViewModel @Inject constructor(
    private val splitRepository: SplitRepository,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<Screen.SplitDayDetail>()
    val splitDayId: Long = route.splitDayId
    val splitId: Long = route.splitId

    private data class DialogState(
        val showExercisePicker: Boolean = false,
        val showRenameDayDialog: Boolean = false,
        val recentlyRemoved: SplitDayExercise? = null
    )

    private val _dialogState = MutableStateFlow(DialogState())

    val uiState: StateFlow<SplitDayDetailUiState> = combine(
        splitRepository.getDayById(splitDayId),
        splitRepository.getExercisesForDay(splitDayId),
        exerciseRepository.getExercises(),
        _dialogState
    ) { day, dayExercises, globalExercises, dialogs ->
        SplitDayDetailUiState(
            day = day,
            splitDayId = splitDayId,
            splitId = splitId,
            dayName = day?.name ?: route.dayName,
            dayExercises = dayExercises,
            allGlobalExercises = globalExercises,
            isLoading = false,
            showExercisePicker = dialogs.showExercisePicker,
            showRenameDayDialog = dialogs.showRenameDayDialog,
            recentlyRemovedExercise = dialogs.recentlyRemoved
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SplitDayDetailUiState(
            splitDayId = splitDayId,
            splitId = splitId,
            dayName = route.dayName
        )
    )

    fun onIncreaseWeight(exercise: Exercise) {
        viewModelScope.launch {
            val step = 1.0f
            val newWeight = exercise.weight + step
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

    fun onReorderExercises(fromIndex: Int, toIndex: Int) {
        val currentList = uiState.value.dayExercises.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            viewModelScope.launch {
                val orderedIds = currentList.map { it.splitDayExerciseId }
                splitRepository.updateDayExerciseSortOrders(orderedIds)
            }
        }
    }

    fun onRemoveExercise(dayExercise: SplitDayExercise) {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(recentlyRemoved = dayExercise)
            splitRepository.removeExerciseFromDay(dayExercise.splitDayExerciseId)
        }
    }

    fun onUndoRemove() {
        val removed = _dialogState.value.recentlyRemoved ?: return
        viewModelScope.launch {
            splitRepository.addExercisesToDay(splitDayId, listOf(removed.exercise.id))
            _dialogState.value = _dialogState.value.copy(recentlyRemoved = null)
        }
    }

    fun onDismissSnackbar() {
        _dialogState.value = _dialogState.value.copy(recentlyRemoved = null)
    }

    fun onAddExercises(exerciseIds: List<Long>) {
        viewModelScope.launch {
            splitRepository.addExercisesToDay(splitDayId, exerciseIds)
            hideExercisePicker()
        }
    }

    fun onRenameDay(newName: String) {
        viewModelScope.launch {
            splitRepository.updateDayName(splitDayId, newName)
            hideRenameDayDialog()
        }
    }

    fun showExercisePicker() { _dialogState.value = _dialogState.value.copy(showExercisePicker = true) }
    fun hideExercisePicker() { _dialogState.value = _dialogState.value.copy(showExercisePicker = false) }

    fun showRenameDayDialog() { _dialogState.value = _dialogState.value.copy(showRenameDayDialog = true) }
    fun hideRenameDayDialog() { _dialogState.value = _dialogState.value.copy(showRenameDayDialog = false) }
}
