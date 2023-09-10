package com.example.lazycook.logic.algorithms

import com.example.lazycook.logic.apis.DatabaseInteractions
import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.IdWithType
import com.example.lazycook.logic.dataclasses.Recipe


//TODO: finish this
//fun DatabaseInteractions.getBasicIngredientsOf(obj: IdWithType): Map<Recipe, Amount> {
//
//}
//
//private fun DatabaseInteractions.getBasicIngredientsWithMultiplier(
//    multiplier: Amount,
//    recipe: Recipe
//): Map<Recipe, Amount> {
//    val ingredients = getRelatedIngredientsSync(recipe.asIdWithType())
//    return if (ingredients.elements.isEmpty()) mapOf(recipe to multiplier)
//    else ingredients.elements.map {
//        getBasicIngredientsWithMultiplier(
//            (it.amount ?: Amount(
//                "unit",
//                1.0
//            )) * (multiplier asMultiplierWithRespectTo recipe.measures), it.recipe
//        )
//    }.fold(
//        emptyMap()
//    ) { total, next ->
//        mapOf(*(total.keys + next.keys).map { it to (total.g) })
//    }
//}
//
//private infix fun Amount?.asMultiplierWithRespectTo(amounts: AmountList): Double {
//    val map = amounts.asMap() + ("unit" to 1.0)
//    val amount = this ?: Amount("unit", 1.0)
//    return amount.amount / (map[amount.unit] ?: 1.0)
//}
//
//private operator fun Amount.times(x: Double): Amount = Amount(unit, amount * x)