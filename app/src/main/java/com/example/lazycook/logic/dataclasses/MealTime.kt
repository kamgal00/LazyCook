package com.example.lazycook.logic.dataclasses

import android.net.Uri
import androidx.compose.foundation.InternalFoundationApi
import androidx.room.Ignore
import com.example.lazycook.logic.DataObject
import com.example.lazycook.other.formatAsHour
import java.util.Date

data class MealTime(
    val id: Int,
    val time: Int,
    val calories: Int?,
    @Ignore
    val relatedTag: Tag?
) : DataObject {
    val name: String
        get() = (relatedTag?.name) ?: "Unnamed"

    override fun toString(): String = "${time.formatAsHour()} - $name"
}


data class Meal(
    val id: Int,
    val startDate: MealDate,
    val endDate: MealDate,
    @Ignore
    val mealTime: MealTime,
    val photo: Uri
) : DataObject {
    fun asIdWithType() = IdWithType(id, "meal")
}

data class MealDate(val date: Date) : DataObject

