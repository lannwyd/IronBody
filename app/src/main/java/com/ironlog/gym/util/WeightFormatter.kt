package com.ironlog.gym.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object WeightFormatter {
    private val decimalFormat = DecimalFormat("0.##", DecimalFormatSymbols(Locale.US))

    fun format(weight: Float): String {
        return decimalFormat.format(weight)
    }

    fun parseOrNull(text: String): Float? {
        val sanitized = text.trim().replace(',', '.')
        return sanitized.toFloatOrNull()?.takeIf { it >= 0f }
    }
}
