package com.example.lazycook.logic.actions

import android.net.Uri
import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.logic.dataclasses.TextFieldReturnVal
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.callCC
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.Create
import com.example.lazycook.logic.returnables.Done

data class IngredientPicker(
    val searchText: String,
    val tagSelector: TagSelector,
    val visibleIngredients: IngredientList,
) : GuiElement

fun <T> ExitContext.acceptIngredient(
    searchText: String = "",
    startingTags: TagList = TagList(emptyList()),
    showAddButton: Boolean = false,
    f: ExitContext.(Ingredient) -> ActionWithContinuation<Unit>
): ActionWithContinuation<T> =
    tagListToTagSelector(startingTags) then { tagSelector ->
        whileCallCC(
            IngredientPicker(
                searchText, tagSelector, IngredientList(emptyList())
            )
        ) { context, _ ->
            val emptyRecipe =
                Recipe(
                    0,
                    Uri.EMPTY,
                    "New recipe",
                    null,
                    AmountList(emptyList()),
//                    IngredientList(emptyList()),
//                    TagList(emptyList())
                )
            databaseInteractions.findAllRecipesSatisfying(
                context.searchText,
                context.tagSelector.currentlySelected
            ) databaseThen {
                userInteractions.show(
                    context.copy(visibleIngredients = it),
                    additionalDescription = if (showAddButton) null else "Select recipe:",
                    additionalOperations = if (showAddButton) listOf(
                        Pair("Add", Create(emptyRecipe))
                    ) else emptyList()
                )
            } checkCases {
                select(TextFieldReturnVal::class) {
                    ret(context.copy(searchText = it.text))
                }
                select(Tag::class) {
                    ret(
                        context.copy(
                            tagSelector = context.tagSelector.copy(
                                currentlySelected = context.tagSelector.currentlySelected.toggle(it)
                            )
                        )
                    )
                }
                select(Ingredient::class) { ingredient ->
                    defaultCallCC(context) {
                        f(ingredient) then { ret(context) }
                    }
                }
                create(Recipe::class) {
                    defaultCallCC(context) {
                        databaseInteractions.add(
                            emptyRecipe.copy(measures = AmountList(listOf(Amount("g", 100.0))))
                        ) databaseThen {
                            showRecipe(it)
                        } then { ret(context) }
                    }
                }
            }
        } then { this@acceptIngredient.onCancel.exit(Unit) }
    }

fun ExitContext.getIngredient(
    searchText: String = "",
    startingTags: TagList = TagList(emptyList())
): ActionWithContinuation<Ingredient> = callCC { ingredientContext ->
    acceptIngredient(searchText, startingTags) { ingredient ->
        chooseAmount(ingredient.recipe.measures, null) then {
            if (it == null) ret(Unit)
            else ingredientContext.exit(ingredient.copy(amount = it))
        }
    }
}


fun ProgramContext.getIngredients(
    searchText: String = "",
    startingTags: TagList = TagList(emptyList()),
    selected: IngredientList = IngredientList(emptyList())
): ActionWithContinuation<IngredientList> =
    defaultCallCC(selected) {
        tagListToTagSelector(startingTags) then { tagSelector ->
            data class Context(val picker: IngredientPicker, val selected: IngredientList)
            whileCallCC(
                Context(
                    picker = IngredientPicker(
                        searchText,
                        tagSelector,
                        IngredientList(emptyList())
                    ),
                    selected = selected
                )
            ) { context, loopScope ->
                databaseInteractions.findAllRecipesSatisfying(
                    context.picker.searchText,
                    context.picker.tagSelector.currentlySelected
                ) databaseThen {
                    userInteractions.show(
                        context.picker.copy(visibleIngredients = it withAmountsFrom context.selected),
                        "Choose ingredients, then accept",
                        listOf(Pair("Accept", Done))
                    )
                } checkCases {
                    select(TextFieldReturnVal::class) {
                        ret(context.copy(picker = context.picker.copy(searchText = it.text)))
                    }
                    select(Tag::class) {
                        ret(
                            context.copy(
                                picker = context.picker.copy(
                                    tagSelector = context.picker.tagSelector.copy(
                                        currentlySelected = context.picker.tagSelector.currentlySelected.toggle(
                                            it
                                        )
                                    )
                                )
                            )
                        )
                    }
                    select(Ingredient::class) { ingredient ->
                        defaultCallCC(context) {
                            chooseAmount(ingredient.recipe.measures, ingredient.amount) then {
                                val chosenWithoutCurrent =
                                    context.selected.elements.filter { it.recipe.id != ingredient.recipe.id }
                                ret(
                                    context.copy(
                                        selected = IngredientList(
                                            if (it == null) chosenWithoutCurrent
                                            else chosenWithoutCurrent + Ingredient(
                                                ingredient.recipe,
                                                it
                                            )
                                        )
                                    )
                                )
                            }
                        }
                    }
                    done {
                        loopScope.exit(context)
                    }
                }
            } then { ret(it.selected) }
        }
    }


