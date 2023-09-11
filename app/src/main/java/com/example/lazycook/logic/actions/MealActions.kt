package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.algorithms.IngredientsCalculator
import com.example.lazycook.logic.algorithms.IngredientsCalculator.Companion.getBasicIngredientsOf
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.AmountList.Companion.asAmountList
import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.MealDate
import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.Delete
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.other.daysUntil
import com.example.lazycook.other.putInList

data class FullInfoMeal(
    val meal: Meal,
    val ingredientList: IngredientList
) : GuiElement

fun ExitContext.fetchFullMeal(meal: Meal): ActionWithContinuation<FullInfoMeal> =
    databaseInteractions.getRelatedIngredients(meal.asIdWithType()) databaseThen {
        ret(
            FullInfoMeal(
                meal,
                it
            )
        )
    }

fun ProgramContext.showMeal(meal: Meal): ActionWithContinuation<Unit> =
    whileCallCC(meal) { meal, loopScope ->
        fetchFullMeal(meal) then { fullMeal ->
            userInteractions.show(
                fullMeal,
                additionalOperations = listOf(
                    "Delete" to Delete(meal),
                    "Add basic ingredients to list" to Select(meal)
                )
            ) checkCases {
                edit(IngredientList::class) {
                    defaultCallCC(meal) {
                        getIngredients(
                            "",
                            TagList(meal.mealTime.relatedTag.putInList()),
                            fullMeal.ingredientList,
                            defaultAmountProducer = getDefaultMeasuresProducerForMeal(
                                meal.startDate,
                                meal.endDate,
                                meal.mealTime
                            )
                        ) then {
                            databaseInteractions.saveRelatedIngredients(meal.asIdWithType(), it)
                        } databaseThen { ret(meal) }
                    }
                }
                delete {
                    defaultCallCC(meal) {
                        databaseInteractions.delete(meal) databaseThen { loopScope.exit(meal) }
                    }
                }
                select(Meal::class) {
                    addBasicIngredientsToSelectedShoppingList(it.asIdWithType()) then { ret(meal) }
                }
                select(Ingredient::class) {
                    showRecipeWithMultiplier(it.recipe, it.amount!!) then { ret(meal) }
                }
            }
        }
    } then { ret(Unit) }

fun getDefaultMeasuresProducerForMeal(
    startDate: MealDate,
    endDate: MealDate,
    mealTime: MealTime
): (Recipe) -> AmountList = {
    val duration =
        startDate.date daysUntil endDate.date
    (it.measures.asMap() + ("unit" to 1.0))
        .mapValues { it.value * duration }
        .let { map ->
            if (mealTime.calories != null && it.measures.asMap().containsKey("kcal")) {
                map + ("kcal" to (mealTime.calories * duration).toDouble())
            } else map
        }
        .asAmountList()
}