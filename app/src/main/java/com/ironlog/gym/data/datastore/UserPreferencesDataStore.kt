package com.ironlog.gym.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
    }

    val weightStepFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.WEIGHT_STEP] ?: 2.5f
    }

    val preferredUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PREFERRED_UNIT] ?: "kg"
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
}
