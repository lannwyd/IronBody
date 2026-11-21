package com.ironlog.gym

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.gym.data.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean?> = userPreferencesDataStore.isDarkModeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun toggleDarkMode(currentIsDark: Boolean) {
        viewModelScope.launch {
            userPreferencesDataStore.setDarkMode(!currentIsDark)
        }
    }
}
