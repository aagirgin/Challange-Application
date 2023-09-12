package com.challengerdaily.challenge.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val currentDate: LocalDate = LocalDate.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
    private val formatter2 = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)
    fun getCurrentFormattedDate(): String {
        return currentDate.format(formatter)
    }

    fun getCurrentFormattedDateAsDayMonth(): String {
        return currentDate.format(formatter2)
    }


    fun getPreviousFormattedDate(): String {
        val previousDate = currentDate.minusDays(1)
        return previousDate.format(formatter)
    }
}