package com.ironlog.gym.ui.screens.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ironlog.gym.domain.model.GymSplit
import com.ironlog.gym.domain.model.SplitDay
import com.ironlog.gym.domain.repository.SplitRepository
import com.ironlog.gym.service.WorkoutSessionManager
import com.ironlog.gym.service.WorkoutSessionState
import com.ironlog.gym.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionScreenUiState(
    val activeSession: WorkoutSessionState? = null,
    val activeSplit: GymSplit? = null,
    val activeSplitDays: List<SplitDay> = emptyList(),
    val isLoading: Boolean = false,
    val showSessionTimerDialog: Boolean = false,
    val showFinishDialog: Boolean = false
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: WorkoutSessionManager,
    private val splitRepository: SplitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionRoute: Screen.Session? = try {
        savedStateHandle.toRoute<Screen.Session>()
    } catch (e: Exception) {
        null
    }

    private val _dialogState = MutableStateFlow(DialogState())

    private data class DialogState(
        val showSessionTimerDialog: Boolean = false,
        val showFinishDialog: Boolean = false
    )

    init {
        // If navigated with explicit dayId, start that workout if not already running
        val initialDayId = sessionRoute?.splitDayId ?: -1L
        val initialSplitId = sessionRoute?.splitId ?: -1L
        val initialDayName = sessionRoute?.dayName ?: ""
        if (initialDayId > 0 && initialSplitId > 0) {
            sessionManager.startWorkout(initialDayId, initialSplitId, initialDayName)
        }
    }

    val uiState: StateFlow<SessionScreenUiState> = splitRepository.getActiveSplit()
        .flatMapLatest { activeSplit ->
            val daysFlow = if (activeSplit != null) {
                splitRepository.getDaysForSplit(activeSplit.id)
            } else {
                flowOf(emptyList())
            }

            combine(
                sessionManager.sessionState,
                daysFlow,
                _dialogState
            ) { sessionState, days, dialogs ->
                SessionScreenUiState(
                    activeSession = sessionState,
                    activeSplit = activeSplit,
                    activeSplitDays = days,
                    isLoading = false,
                    showSessionTimerDialog = dialogs.showSessionTimerDialog,
                    showFinishDialog = dialogs.showFinishDialog
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionScreenUiState()
        )

    fun startWorkout(splitDayId: Long, splitId: Long, dayName: String) {
        sessionManager.startWorkout(splitDayId, splitId, dayName)
    }

    fun toggleExerciseChrono(exerciseIndex: Int? = null) {
        sessionManager.toggleExerciseChrono(exerciseIndex)
    }

    fun nextExercise() {
        sessionManager.nextExercise()
    }

    fun startRestTimer(exerciseIndex: Int, seconds: Int) {
        sessionManager.startRestTimer(exerciseIndex, seconds)
    }

    fun cancelRestTimer(exerciseIndex: Int) {
        sessionManager.cancelRestTimer(exerciseIndex)
    }

    fun toggleExerciseDone(exerciseIndex: Int) {
        sessionManager.toggleExerciseDone(exerciseIndex)
    }

    fun showSessionTimerDialog() {
        _dialogState.value = _dialogState.value.copy(showSessionTimerDialog = true)
    }

    fun dismissSessionTimerDialog() {
        _dialogState.value = _dialogState.value.copy(showSessionTimerDialog = false)
    }

    fun setSessionTimer(totalMinutes: Int) {
        sessionManager.setSessionTimer(totalMinutes)
        dismissSessionTimerDialog()
    }

    fun cancelSessionTimer() {
        sessionManager.cancelSessionTimer()
    }

    fun showFinishSessionDialog() {
        _dialogState.value = _dialogState.value.copy(showFinishDialog = true)
    }

    fun dismissFinishSessionDialog() {
        _dialogState.value = _dialogState.value.copy(showFinishDialog = false)
    }

    fun confirmFinishSession() {
        sessionManager.finishSession {
            _dialogState.value = _dialogState.value.copy(showFinishDialog = false)
        }
    }
}
