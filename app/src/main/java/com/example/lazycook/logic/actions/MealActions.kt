package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.ret
import com.example.lazycook.other.putInList

data class FullInfoMeal(
    val meal: Meal,
    val ingredientList: IngredientList
) : GuiElement

fun ExitContext.fetchFullMeal(meal: Meal): ActionWithContinuation<FullInfoMeal> =
    databaseInteractions.getRelatedIngredients(meal.asIdWithType()) databaseThen { ret(FullInfoMeal(meal, it)) }

fun ProgramContext.showMeal(meal: Meal): ActionWithContinuation<Unit> =
    whileCallCC(meal) { meal, loopScope ->
        fetchFullMeal(meal) then { fullMeal ->
            userInteractions.show(fullMeal) checkCases {
                edit(IngredientList::class) {
                    defaultCallCC(meal) {
                        getIngredients(
                            "",
                            TagList(meal.mealTime.relatedTag.putInList()),
                            fullMeal.ingredientList
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
            }
        }
    } then { ret(Unit) }
