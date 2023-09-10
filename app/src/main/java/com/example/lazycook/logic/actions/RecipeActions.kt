package com.example.lazycook.logic.actions

import android.net.Uri
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.algorithms.asMultiplierWithRespectTo
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.AmountList.Companion.asAmountList
import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.dataclasses.TitleAndDescription
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.Delete
import com.example.lazycook.logic.returnables.PhotoGallery
import com.example.lazycook.logic.returnables.PhotoTake
import com.example.lazycook.logic.returnables.Select

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
                additionalOperations = listOf(
                    "Delete" to Delete(recipe),
                    "Add to shopping list" to Select(recipe)
                )
            ) checkCases {
                select(PhotoGallery::class) {
                    defaultCallCC(recipe) {
                        databaseInteractions.edit(
                            recipe.copy(photo = it.uri ?: recipe.photo)
                        ) databaseThen { ret(it) }
                    }
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
                        getIngredients(selected = it, defaultAmountProducer = null) then {
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
                select(Recipe::class) {
                    defaultCallCC(recipe) {
                        chooseAmount(
                            (recipe.measures.asMap() + ("unit" to 1.0)).asAmountList(),
                            previousAmount = null
                        ) then {
                            addBasicIngredientsToSelectedShoppingList(
                                recipe,
                                multiplier = it!!
                            ) then {
                                ret(recipe)
                            }
                        }
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