package com.example.challapp.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
object DateUtils {
    private val currentDate: LocalDate = LocalDate.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    fun getCurrentFormattedDate(): String {
        return currentDate.format(formatter)
    }

    fun getPreviousFormattedDate(): String {
        val previousDate = currentDate.minusDays(1)
        return previousDate.format(formatter)
    }
}