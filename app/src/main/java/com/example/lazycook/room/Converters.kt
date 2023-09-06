package com.example.lazycook.room

import androidx.room.TypeConverter
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.AmountList.Companion.asAmountList
import com.example.lazycook.logic.dataclasses.MealDate
import com.google.gson.Gson
import java.util.Date

class Converters {
    @TypeConverter
    fun toMealDate(value: Long): MealDate {
        return MealDate(Date(value))
    }

    @TypeConverter
    fun fromMealDate(mealDate: MealDate): Long {
        return mealDate.date.time
    }

    @TypeConverter
    fun toAmountList(json: String): AmountList = Gson().fromJson(json, AmountList::class.java)

    @TypeConverter
    fun fromAmountList(amountList: AmountList): String = Gson().toJson(amountList)
}
