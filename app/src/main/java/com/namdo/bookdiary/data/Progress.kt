package com.namdo.bookdiary.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.namdo.bookdiary.util.toDayAndMonthAndYear

@Entity
data class Progress(
    @PrimaryKey(autoGenerate = true) val progressId: Int,
    val bookId: String,
    val pagesRead: Int,
    val minutesRead: Int,
    val date: Long,
)

fun List<Progress>.pagesReadForLastSevenDays() = this
    .groupBy { it.date.toDayAndMonthAndYear() }
    .map {
        Pair(it.key.dropLast(4), it.value.sumOf { it.pagesRead })
    }
    .takeLast(7)

fun List<Progress>.minutesReadForLastSevenDays() = this
    .groupBy { it.date.toDayAndMonthAndYear() }
    .map {
        Pair(it.key.dropLast(4), it.value.sumOf { it.minutesRead })
    }
    .takeLast(7)