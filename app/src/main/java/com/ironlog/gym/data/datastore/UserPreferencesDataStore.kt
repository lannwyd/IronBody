package com.ironlog.gym.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ironlog_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val WEIGHT_STEP = floatPreferencesKey("weight_step")
        val PREFERRED_UNIT = stringPreferencesKey("preferred_unit")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    val weightStepFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.WEIGHT_STEP] ?: 2.5f
    }

    val preferredUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PREFERRED_UNIT] ?: "kg"
    }

    val isDarkModeFlow: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_DARK_MODE]
    }

    suspend fun setWeightStep(step: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEIGHT_STEP] = step
        }
    }

    suspend fun setPreferredUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREFERRED_UNIT] = unit
        }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = isDark
        }
    }
}
