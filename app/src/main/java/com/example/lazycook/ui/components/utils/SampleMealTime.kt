package com.example.lazycook.ui.components.utils

import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.Tag

    object SampleMealTime {
        val Breakfast = MealTime(1, 8 * 60, 600, SampleTag.Breakfast)
        val SecondBreakfast = MealTime(2, 12 * 60, 400, SampleTag.SecondBreakfast)
        val Lunch = MealTime(3, 15 * 60, 800, SampleTag.Lunch)
        val Dinner = MealTime(4, 19 * 60, 400, SampleTag.Dinner)
    }
