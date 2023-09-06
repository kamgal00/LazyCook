package com.example.lazycook.other

import com.example.lazycook.logic.dataclasses.MealDate
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun <T> T?.putInList(): List<T> = if (this != null) listOf(this) else emptyList()

fun MealDate.toCalendar(): Calendar = Calendar.getInstance().apply { time = this@toCalendar.date }
fun Calendar.format(pattern: String? = null): String =
    pattern?.let {
        SimpleDateFormat(it, Locale.getDefault()).format(
            time
        )
    } ?: DateFormat.getDateInstance().format(time)

fun Calendar.addDays(amount: Int): Calendar = this.apply { add(Calendar.DATE, amount) }
fun Int.formatAsHour(): String = "${this / 60}:${
    (this % 60).toString().padStart(2, '0')
}"