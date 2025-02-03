package com.namdo.bookdiary.ui.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.namdo.bookdiary.db.ProgressDao
import com.namdo.bookdiary.util.toHourAndMinute
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val progressDao: ProgressDao
) : ViewModel() {

    var timeRead by mutableStateOf("")
    var pagesRead by mutableStateOf("")

    init {
        getStats()
    }

    private fun getStats() {
        // Find start timestamp for this week
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            timeRead = progressDao.getMinutesReadThisWeek(calendar.timeInMillis)?.toHourAndMinute() ?: "0 h 0 m"
            pagesRead = progressDao.getPagesReadThisWeek(calendar.timeInMillis)?.toString() ?: "0"
        }
    }
}