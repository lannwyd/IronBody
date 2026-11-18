package com.ironlog.gym.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterTest {

    @Test
    fun kgToLbs_calculatesCorrectly() {
        val kg = 100f
        val lbs = UnitConverter.kgToLbs(kg)
        assertEquals(220.5f, lbs, 0.1f)
    }

    @Test
    fun lbsToKg_calculatesCorrectly() {
        val lbs = 220f
        val kg = UnitConverter.lbsToKg(lbs)
        assertEquals(99.8f, kg, 0.1f)
    }

    @Test
    fun convert_sameUnit_returnsOriginal() {
        val weight = 75.5f
        val result = UnitConverter.convert(weight, "kg", "kg")
        assertEquals(weight, result, 0.001f)
    }

    @Test
    fun convert_zeroWeight_returnsZero() {
        assertEquals(0f, UnitConverter.kgToLbs(0f), 0.001f)
        assertEquals(0f, UnitConverter.lbsToKg(0f), 0.001f)
    }
}
