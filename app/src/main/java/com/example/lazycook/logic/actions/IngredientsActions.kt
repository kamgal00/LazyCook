package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.AmountList.Companion.asAmountList
import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.logic.dataclasses.TextFieldReturnVal
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.dataclasses.TitleAndDescription
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.callCC
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.Create
import com.example.lazycook.logic.returnables.Delete
import com.example.lazycook.logic.returnables.Done
import com.example.lazycook.logic.returnables.PhotoGallery
import com.example.lazycook.logic.returnables.PhotoTake

data class IngredientPicker(
    val searchText: String,
    val tagSelector: TagSelector,
    val visibleIngredients: IngredientList,
) : GuiElement

private fun <T> ExitContext.acceptIngredient(
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
                    null,
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

data class FullInfoRecipe(
    val recipe: Recipe,
    val ingredientList: IngredientList,
    val tagList: TagList
) : GuiElement

fun ExitContext.fetchFullRecipe(recipe: Recipe): ActionWithContinuation<FullInfoRecipe> =
    databaseInteractions.getRelatedIngredients(recipe.asIdWithType()) databaseThen { ingredientList ->
        databaseInteractions.getRelatedTags(recipe.asIdWithType()) databaseThen { tagList ->
            ret(FullInfoRecipe(recipe, ingredientList, tagList))
        }
    }

fun ProgramContext.showRecipe(recipe: Recipe): ActionWithContinuation<Unit> =
    whileCallCC(recipe) { recipe, loopScope ->
        fetchFullRecipe(recipe) then { fullInfoRecipe ->
            userInteractions.show(
                fullInfoRecipe,
                additionalOperations = listOf(Pair("Delete", Delete(recipe)))
            ) checkCases {
                select(PhotoGallery) {
                    ret(recipe) // TODO
                }
                select(PhotoTake) {
                    ret(recipe) // TODO
                }
                edit(TitleAndDescription::class) {
                    defaultCallCC(recipe) {
                        getNewTitleAndDescription(it) then {
                            databaseInteractions.edit(
                                recipe.copy(
                                    name = it.title,
                                    description = it.description
                                )
                            ) databaseThen { ret(it) }
                        }
                    }
                }
                create(AmountList::class) {
                    defaultCallCC(recipe) {
                        getNewAmount() then {
                            databaseInteractions.edit(recipe.copy(measures = AmountList(recipe.measures.listElements + it).normalize()))
                        } databaseThen {
                            ret(it)
                        }
                    }
                }
                edit(Amount::class) { oldAmount ->
                    defaultCallCC(recipe) {
                        editOrDeleteAmount(oldAmount) then { newAmount ->
                            val measuresMap = recipe.measures.asMap()
                            val newAmountList =
                                (if (newAmount == null) (measuresMap - oldAmount.unit)
                                else (measuresMap + Pair(
                                    newAmount.unit,
                                    newAmount.amount
                                ))).asAmountList()
                            ret(newAmountList)
                        } then {
                            databaseInteractions.edit(recipe.copy(measures = it.normalize()))
                        } databaseThen {
                            ret(it)
                        }
                    }
                }
                edit(TagList::class) {
                    defaultCallCC(recipe) {
                        chooseTags(it) then {
                            databaseInteractions.saveRelatedTags(recipe.asIdWithType(), it)
                        } databaseThen {
                            ret(recipe)
                        }
                    }
                }
                edit(IngredientList::class) {
                    defaultCallCC(recipe) {
                        getIngredients(selected = it) then {
                            databaseInteractions.saveRelatedIngredients(recipe.asIdWithType(), it)
                        } databaseThen {
                            ret(recipe)
                        }
                    }
                }
                select(Ingredient::class) {
                    showRecipe(it.recipe) then { ret(recipe) }
                }
                delete {
                    defaultCallCC(recipe) {
                        databaseInteractions.delete(recipe) databaseThen { loopScope.exit(recipe) }
                    }
                }
            }
        }
    } then { ret(Unit) }

fun ProgramContext.showAllRecipes(): ActionWithContinuation<Unit> =
    defaultCallCC(Unit) { acceptIngredient(showAddButton = true) { showRecipe(it.recipe) } }


fun ExitContext.getNewTitleAndDescription(old: TitleAndDescription): ActionWithContinuation<TitleAndDescription> =
    userInteractions.show(old) checkCases {
        edit(TitleAndDescription::class) {
            ret(it)
        }
    }

