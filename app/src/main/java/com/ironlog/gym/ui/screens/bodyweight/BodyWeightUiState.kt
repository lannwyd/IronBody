package com.ironlog.gym.ui.screens.bodyweight

import com.ironlog.gym.domain.model.BodyWeight

data class BodyWeightUiState(
    val entries: List<BodyWeight> = emptyList(),
    val isLoading: Boolean = true,

    // Add / Edit dialog state
    val showDialog: Boolean = false,
    val editingEntry: BodyWeight? = null,
    val dialogWeight: String = "",
    val dialogWeightError: Int? = null,
    val dialogUnit: String = "kg",
    val dialogDate: Long = System.currentTimeMillis(),
    val dialogReceiptImagePath: String? = null,
    val dialogNote: String = "",
    val dialogIsLoading: Boolean = false,

    // Full-screen receipt viewer
    val viewingReceiptPath: String? = null
)
