package com.example.lazycook.ui.components.utils

import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList

object SampleTag {
    val Breakfast = Tag(1, "Śniadanie")
    val SecondBreakfast = Tag(2, "Drugie śniadanie")
    val Lunch = Tag(3, "Obiad")
    val Dinner = Tag(4, "Kolacja")
    val Vegetarian = Tag(3, "Wegetariańskie")
    val SampleTagList: TagList = TagList(listOf(Breakfast, SecondBreakfast, Lunch, Dinner, Vegetarian))
}


