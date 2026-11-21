package com.ironlog.gym.ui.screens.session.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.gym.domain.model.WorkoutSession
import com.ironlog.gym.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionHistoryUiState(
    val sessions: List<WorkoutSession> = emptyList(),
    val targetSessionToDelete: WorkoutSession? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class SessionHistoryViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _targetSessionToDelete = MutableStateFlow<WorkoutSession?>(null)

    val uiState: StateFlow<SessionHistoryUiState> = combine(
        sessionRepository.getAllSessions().map { list ->
            list.filter { it.endTime != null }
        },
        _targetSessionToDelete
    ) { completedSessions, targetToDelete ->
        SessionHistoryUiState(
            sessions = completedSessions,
            targetSessionToDelete = targetToDelete,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionHistoryUiState(isLoading = true)
    )

    fun requestDeleteSession(session: WorkoutSession) {
        _targetSessionToDelete.value = session
    }

    fun dismissDeleteDialog() {
        _targetSessionToDelete.value = null
    }

    fun confirmDeleteSession() {
        val session = _targetSessionToDelete.value ?: return
        viewModelScope.launch {
            sessionRepository.deleteSession(session.id)
            _targetSessionToDelete.value = null
        }
    }
}
