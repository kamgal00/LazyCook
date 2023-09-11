package com.example.lazycook.ui.components.utils

import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.MealDate
import com.example.lazycook.logic.dataclasses.MealTime
import java.util.Date
import java.text.DateFormat
import java.util.Locale

object SampleMeal {
    val SampleBreakfast = Meal(
        1,
        MealDate(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).parse("10.09.2023")!!),
        MealDate(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).parse("13.09.2023")!!),
        SampleMealTime.Breakfast,
        SampleRecipe.cat.photo
    )
    val SampleLunch = Meal(
        2,
        MealDate(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).parse("11.09.2023")!!),
        MealDate(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).parse("14.09.2023")!!),
        SampleMealTime.Lunch,
        SampleRecipe.cat.photo
    )
    val SampleDinner = Meal(
        3,
        MealDate(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).parse("12.09.2023")!!),
        MealDate(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).parse("13.09.2023")!!),
        SampleMealTime.Dinner,
        SampleRecipe.cat.photo
    )
}
