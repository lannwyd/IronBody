package com.ironlog.gym.ui.screens.exerciseEdit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironlog.gym.R
import com.ironlog.gym.data.datastore.UserPreferencesDataStore
import com.ironlog.gym.domain.model.Exercise
import com.ironlog.gym.domain.model.LibraryExerciseItem
import com.ironlog.gym.domain.repository.ExerciseRepository
import com.ironlog.gym.util.ImageStorageManager
import com.ironlog.gym.util.WeightFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseEditViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore,
    private val imageStorageManager: ImageStorageManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseEditUiState())
    val uiState: StateFlow<ExerciseEditUiState> = _uiState.asStateFlow()

    init {
        val exerciseId = savedStateHandle.get<Long>("exerciseId")
        val initialSection = savedStateHandle.get<String>("initialSection")
        if (exerciseId != null && exerciseId > 0) {
            loadExercise(exerciseId)
        } else {
            viewModelScope.launch {
                val preferredUnit = userPreferencesDataStore.preferredUnitFlow.first()
                _uiState.update { 
                    it.copy(
                        unit = preferredUnit,
                        section = initialSection ?: it.section
                    ) 
                }
            }
        }
    }

    private fun loadExercise(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val exercise = exerciseRepository.getExercise(id).first()
            if (exercise != null) {
                _uiState.update {
                    it.copy(
                        exerciseId = exercise.id,
                        name = exercise.name,
                        weight = WeightFormatter.format(exercise.weight),
                        unit = exercise.unit,
                        section = exercise.section,
                        imagePath = exercise.imagePath,
                        note = exercise.note,
                        isEditMode = true,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(
                name = name,
                nameError = if (name.isNotBlank()) null else it.nameError
            )
        }
    }

    fun onWeightChange(weight: String) {
        _uiState.update {
            it.copy(
                weight = weight,
                weightError = if (weight.isNotBlank()) null else it.weightError
            )
        }
    }

    fun onUnitChange(unit: String) {
        _uiState.update { it.copy(unit = unit) }
    }

    fun onSectionChange(section: String) {
        _uiState.update { it.copy(section = section) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onImagePicked(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val savedPath = imageStorageManager.saveImageFromUri(uri)
            if (savedPath != null) {
                // If previous image exists and is different, remove the old one
                val oldPath = _uiState.value.imagePath
                if (!oldPath.isNullOrBlank() && oldPath != savedPath) {
                    imageStorageManager.deleteImageFile(oldPath)
                }
                _uiState.update { it.copy(imagePath = savedPath, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onRemoveImage() {
        val currentPath = _uiState.value.imagePath
        if (!currentPath.isNullOrBlank()) {
            viewModelScope.launch {
                imageStorageManager.deleteImageFile(currentPath)
            }
        }
        _uiState.update { it.copy(imagePath = null) }
    }

    fun onApplyImageAdjustment(
        scale: Float,
        panX: Float,
        panY: Float,
        rotation: Float,
        viewW: Int,
        viewH: Int
    ) {
        val currentPath = _uiState.value.imagePath ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val adjustedPath = imageStorageManager.saveAdjustedBitmap(
                sourcePath = currentPath,
                scale = scale,
                panX = panX,
                panY = panY,
                rotationDegrees = rotation,
                viewportWidth = viewW,
                viewportHeight = viewH
            )
            if (adjustedPath != null) {
                if (currentPath != adjustedPath && File(currentPath).exists()) {
                    imageStorageManager.deleteImageFile(currentPath)
                }
                _uiState.update { it.copy(imagePath = adjustedPath, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onLibraryImageSelected(resId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val savedPath = imageStorageManager.saveImageFromResource(resId)
            if (savedPath != null) {
                val oldPath = _uiState.value.imagePath
                if (!oldPath.isNullOrBlank() && oldPath != savedPath) {
                    imageStorageManager.deleteImageFile(oldPath)
                }
                _uiState.update { it.copy(imagePath = savedPath, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSelectLibraryExercise(item: LibraryExerciseItem) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val savedPath = imageStorageManager.saveImageFromResource(item.iconRes)
            if (savedPath != null) {
                val oldPath = _uiState.value.imagePath
                if (!oldPath.isNullOrBlank() && oldPath != savedPath) {
                    imageStorageManager.deleteImageFile(oldPath)
                }
                _uiState.update {
                    it.copy(
                        name = item.name,
                        nameError = null,
                        section = item.section.id,
                        imagePath = savedPath,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        name = item.name,
                        nameError = null,
                        section = item.section.id,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSaveExercise() {
        val currentState = _uiState.value
        val trimmedName = currentState.name.trim()

        var hasError = false
        var nameErrorRes: Int? = null
        var weightErrorRes: Int? = null

        if (trimmedName.isEmpty()) {
            nameErrorRes = R.string.error_empty_name
            hasError = true
        }

        val parsedWeight = WeightFormatter.parseOrNull(currentState.weight)
        if (currentState.weight.isBlank()) {
            weightErrorRes = R.string.error_empty_weight
            hasError = true
        } else if (parsedWeight == null) {
            weightErrorRes = R.string.error_invalid_weight
            hasError = true
        } else if (parsedWeight < 0f) {
            weightErrorRes = R.string.error_negative_weight
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    nameError = nameErrorRes,
                    weightError = weightErrorRes
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val weightValue = parsedWeight ?: 0f

            if (currentState.isEditMode && currentState.exerciseId != null) {
                val existing = exerciseRepository.getExercise(currentState.exerciseId).first()
                val updated = (existing ?: Exercise(id = currentState.exerciseId, name = trimmedName, weight = weightValue)).copy(
                    name = trimmedName,
                    weight = weightValue,
                    unit = currentState.unit,
                    section = currentState.section,
                    imagePath = currentState.imagePath,
                    note = currentState.note.trim()
                )
                exerciseRepository.updateExercise(updated)
            } else {
                val newExercise = Exercise(
                    name = trimmedName,
                    weight = weightValue,
                    unit = currentState.unit,
                    section = currentState.section,
                    imagePath = currentState.imagePath,
                    note = currentState.note.trim()
                )
                exerciseRepository.addExercise(newExercise)
            }

            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }

    fun onDeleteExercise() {
        val id = _uiState.value.exerciseId ?: return
        viewModelScope.launch {
            val existing = exerciseRepository.getExercise(id).first()
            if (existing != null) {
                exerciseRepository.deleteExercise(existing, deleteImageFile = true)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
