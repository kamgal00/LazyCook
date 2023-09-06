package com.example.lazycook.ui.components.utils

import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Recipe

object SampleRecipe {
    val SampleIngredientList = IngredientList((0 until 10).map {
            Ingredient(
                Recipe(
                    id = (it + 10),
                    photo = "https://www.pastelowelove.pl/userdata/public/gfx/5582/kotek-mruczek--naklejka.-naklejka-dla-dzieci.-dekoracje-pokoju.jpg",
                    name = "piesek nr $it",
                    description = null,
                    measures = AmountList(listOf(
                        Amount("kcal", 1000.0),
                        Amount("g", 5000.0)
                    ).flatMap { item -> (0 until 20).map { item } }),
                ), Amount("kcal", 100.0)
            )
        })
    val cat = Recipe(
        id = 1,
        photo = "https://www.pastelowelove.pl/userdata/public/gfx/5582/kotek-mruczek--naklejka.-naklejka-dla-dzieci.-dekoracje-pokoju.jpg",
        name = "kotek",
        description = (0 until 100).joinToString { "1984" },
        measures = AmountList(listOf(
            Amount("kcal", 1000.0),
            Amount("g", 5000.0)
        ).flatMap { item -> (0 until 20).map { item } }),
    )
}