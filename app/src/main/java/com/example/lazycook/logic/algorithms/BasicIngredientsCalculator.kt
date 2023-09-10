package com.example.lazycook.logic.algorithms

import com.example.lazycook.logic.apis.DatabaseInteractions
import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.AmountList.Companion.asAmountList
import com.example.lazycook.logic.dataclasses.IdWithType
import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.other.mergeMaps


class IngredientsCalculator private constructor(private val db: DatabaseInteractions) {
    private val visitedRecipes: MutableSet<Int> = mutableSetOf()

    private fun getBasicIngredientsWithMultiplier(
        amount: Amount,
        recipe: Recipe
    ): Map<Recipe, Amount> {
        if (visitedRecipes.contains(recipe.id)) return emptyMap()
        visitedRecipes += recipe.id
        val ingredients =
            db.getRelatedIngredientsSync(recipe.asIdWithType()).elements.map { it.recipe to it.amount!! }
        val returnVal = if (ingredients.isEmpty()) mapOf(recipe to amount)
        else {
            val multiplier = amount asMultiplierWithRespectTo recipe.measures
            ingredients.map { getBasicIngredientsWithMultiplier(it.second, it.first) * multiplier }
                .combine()
        }

        visitedRecipes -= recipe.id
        return returnVal
    }

    companion object {
        fun DatabaseInteractions.getBasicIngredientsOf(
            obj: IdWithType,
            multiplier: Double = 1.0
        ): IngredientList {
            val calculator = IngredientsCalculator(this)
            val allMaps = getRelatedIngredientsSync(obj).elements.map {
                calculator.getBasicIngredientsWithMultiplier(it.amount!!, it.recipe) * multiplier
            }
            return allMaps.toIngredientList()
        }

        fun DatabaseInteractions.getBasicIngredientsOfRecipe(
            recipe: Recipe,
            amount: Amount
        ): IngredientList = listOf(
            IngredientsCalculator(this).getBasicIngredientsWithMultiplier(
                amount,
                recipe
            )
        ).toIngredientList()
    }
}


private fun AmountList.asMapWithUnit() = this.asMap() + ("unit" to 1.0)
infix fun Amount.asMultiplierWithRespectTo(amounts: AmountList): Double =
    amount / (amounts.asMapWithUnit()[unit] ?: 1.0)

private fun Amount.mergeToAmount(next: Amount, amounts: AmountList): Amount = Amount(
    next.unit,
    next.amount + amounts.asMapWithUnit()[next.unit]!! * asMultiplierWithRespectTo(
        amounts.asMapWithUnit().asAmountList()
    )
)

private operator fun Map<Recipe, Amount>.times(x: Double): Map<Recipe, Amount> =
    mapValues { Amount(it.value.unit, it.value.amount * x) }

private fun List<Map<Recipe, Amount>>.combine() =
    mergeMaps(*this.toTypedArray()) { recipe, amount1, amount2 ->
        amount2.mergeToAmount(amount1, recipe.measures)
    }

fun List<Map<Recipe, Amount>>.toIngredientList(): IngredientList =
    IngredientList(combine().map { Ingredient(it.key, it.value) })