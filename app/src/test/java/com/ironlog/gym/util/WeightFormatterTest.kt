package com.ironlog.gym.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeightFormatterTest {

    @Test
    fun format_wholeNumber_removesTrailingZeros() {
        assertEquals("50", WeightFormatter.format(50f))
        assertEquals("100", WeightFormatter.format(100.0f))
    }

    @Test
    fun format_decimal_formatsCorrectly() {
        assertEquals("52.5", WeightFormatter.format(52.5f))
        assertEquals("52.25", WeightFormatter.format(52.25f))
    }

    @Test
    fun parseOrNull_validNumbers_parsesCorrectly() {
        assertEquals(52.5f, WeightFormatter.parseOrNull("52.5") ?: 0f, 0.001f)
        assertEquals(52.5f, WeightFormatter.parseOrNull("52,5") ?: 0f, 0.001f)
        assertEquals(0f, WeightFormatter.parseOrNull("0") ?: -1f, 0.001f)
    }

    @Test
    fun parseOrNull_invalidNumbers_returnsNull() {
        assertNull(WeightFormatter.parseOrNull(""))
        assertNull(WeightFormatter.parseOrNull("abc"))
        assertNull(WeightFormatter.parseOrNull("-10"))
    }
}
