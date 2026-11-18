package com.ironlog.gym.util

import kotlin.math.roundToInt

object UnitConverter {
    const val KG_TO_LBS_FACTOR = 2.20462262185f
    const val LBS_TO_KG_FACTOR = 0.45359237f

    fun kgToLbs(kg: Float): Float {
        if (kg <= 0f) return 0f
        val converted = kg * KG_TO_LBS_FACTOR
        return (converted * 10f).roundToInt() / 10f
    }

    fun lbsToKg(lbs: Float): Float {
        if (lbs <= 0f) return 0f
        val converted = lbs * LBS_TO_KG_FACTOR
        return (converted * 10f).roundToInt() / 10f
    }

    fun convert(weight: Float, fromUnit: String, toUnit: String): Float {
        if (fromUnit.equals(toUnit, ignoreCase = true)) return weight
        return if (toUnit.equals("lbs", ignoreCase = true)) {
            kgToLbs(weight)
        } else {
            lbsToKg(weight)
        }
    }
}
