package com.example.lazycook.other

import com.example.lazycook.logic.dataclasses.MealDate
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun <T> T?.putInList(): List<T> = if (this != null) listOf(this) else emptyList()

fun MealDate.toCalendar(): Calendar = Calendar.getInstance().apply { time = this@toCalendar.date }
fun Calendar.format(pattern: String? = null): String =
    pattern?.let {
        SimpleDateFormat(it, Locale.getDefault()).format(
            time
        )
    } ?: DateFormat.getDateInstance().format(time)

fun today(): Date = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
}.time

fun Calendar.addDays(amount: Int): Calendar = this.apply { add(Calendar.DATE, amount) }
fun Int.formatAsHour(): String = "${this / 60}:${
    (this % 60).toString().padStart(2, '0')
}"

infix fun Date.daysUntil(other: Date): Int =
    (other.time - time).let { TimeUnit.DAYS.convert(it, TimeUnit.MILLISECONDS).toInt() }

fun <K, V> mergeMaps(vararg maps: Map<K, V>, combiner: (K, V, V) -> V = { _, x, _ -> x }) =
    maps.flatMap { it.entries }.groupBy { it.key }
        .mapValues { recipeToOldEntry ->
            recipeToOldEntry.value.map { it.value }
                .reduce { x, y -> combiner(recipeToOldEntry.key, x, y) }
        }