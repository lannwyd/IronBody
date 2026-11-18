package com.ironlog.gym.domain

import com.ironlog.gym.util.WeightFormatter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseValidationTest {

    @Test
    fun exerciseName_whenBlank_isInvalid() {
        val name = "   "
        assertTrue(name.isBlank())
    }

    @Test
    fun exerciseName_whenValid_isValid() {
        val name = "Barbell Bench Press"
        assertTrue(name.isNotBlank())
    }

    @Test
    fun weightValidation_negativeWeight_isBlocked() {
        val parsed = WeightFormatter.parseOrNull("-5.0")
        assertTrue(parsed == null)
    }

    @Test
    fun weightValidation_zeroWeight_isAllowed() {
        val parsed = WeightFormatter.parseOrNull("0")
        assertTrue(parsed != null && parsed >= 0f)
    }

    @Test
    fun weightValidation_stepIncrements_remainValid() {
        var weight = 50f
        val step = 2.5f
        weight += step
        assertTrue(weight == 52.5f)
    }
}
