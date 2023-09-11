package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.ShoppingList
import com.example.lazycook.logic.dataclasses.TextFieldReturnVal
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.algorithms.IngredientsCalculator.Companion.getBasicIngredientsOf
import com.example.lazycook.logic.algorithms.IngredientsCalculator.Companion.getBasicIngredientsOfRecipe
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.IdWithType
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.Create


data class ShoppingListSelector(
    val lists: List<ShoppingList>,
) : GuiElement

fun ExitContext.selectShoppingList(): ActionWithContinuation<ShoppingList> =
    databaseInteractions.getList(ShoppingList::class) databaseThen {
        userInteractions.show(ShoppingListSelector(it))
    } checkCases {
        select(ShoppingList::class) {
            ret(it)
        }
    }

fun ProgramContext.addBasicIngredientsToSelectedShoppingList(
    listOwner: IdWithType,
    multiplier: Double = 1.0
): ActionWithContinuation<Unit> =
    defaultCallCC(Unit) {
        selectShoppingList() then { selectedList ->
            databaseInteractions.getRelatedIngredients(selectedList.asIdWithType()) databaseThen { currentIngredients ->
                val finalIngredientList =
                    currentIngredients + databaseInteractions.getBasicIngredientsOf(
                        listOwner, multiplier
                    )
                databaseInteractions.saveRelatedIngredients(
                    selectedList.asIdWithType(),
                    finalIngredientList
                ) databaseThen { ret(Unit) }
            }
        }
    }

fun ProgramContext.addBasicIngredientsToSelectedShoppingList(
    recipe: Recipe,
    multiplier: Amount
): ActionWithContinuation<Unit> =
    defaultCallCC(Unit) {
        selectShoppingList() then { selectedList ->
            databaseInteractions.getRelatedIngredients(selectedList.asIdWithType()) databaseThen { currentIngredients ->
                val finalIngredientList =
                    currentIngredients + databaseInteractions.getBasicIngredientsOfRecipe(
                        recipe, multiplier
                    )
                databaseInteractions.saveRelatedIngredients(
                    selectedList.asIdWithType(),
                    finalIngredientList
                ) databaseThen { ret(Unit) }
            }
        }
    }

fun ProgramContext.showShoppingLists(): ActionWithContinuation<Unit> =
    whileCallCC(Unit) { _, _ ->
        databaseInteractions.getList(ShoppingList::class) databaseThen {
            userInteractions.show(
                ShoppingListSelector(it),
                additionalOperations = listOf(
                    Pair(
                        "Create new shopping list",
                        Create(ShoppingList(0, ""))
                    )
                )
            ) checkCases {
                select(ShoppingList::class) {
                    showShoppingList(it)
                }
                create(ShoppingList::class) {
                    defaultCallCC(Unit) {
                        databaseInteractions.add(
                            ShoppingList(0, "New list")
                        ) databaseThen {
                            showShoppingList(it)
                        }
                    }
                }
            }
        }
    }

data class FullInfoShoppingList(
    val shoppingList: ShoppingList,
    val ingredientList: IngredientList
) : GuiElement

fun ExitContext.fetchFullShoppingList(shoppingList: ShoppingList): ActionWithContinuation<FullInfoShoppingList> =
    databaseInteractions.getRelatedIngredients(shoppingList.asIdWithType()) databaseThen {
        ret(FullInfoShoppingList(shoppingList, it))
    }

fun ProgramContext.showShoppingList(shoppingList: ShoppingList): ActionWithContinuation<Unit> =
    whileCallCC(shoppingList) { shoppingList, loopScope ->
        fetchFullShoppingList(shoppingList) then { fullShoppingList ->
            userInteractions.show(fullShoppingList) checkCases {
                edit(TextFieldReturnVal::class) {
                    defaultCallCC(shoppingList) {
                        databaseInteractions.edit(shoppingList.copy(name = it.text)) databaseThen {
                            ret(it)
                        }
                    }
                }
                delete {
                    defaultCallCC(shoppingList) {
                        confirm("Are you sure you want to delete shopping list ${shoppingList.name}?") then {
                            if (it) {
                                databaseInteractions.delete(shoppingList) databaseThen {
                                    loopScope.exit(shoppingList)
                                }
                            } else ret(shoppingList)
                        }
                    }
                }
                select(Ingredient::class) {
                    defaultCallCC(shoppingList) {
                        databaseInteractions.saveRelatedIngredients(
                            shoppingList.asIdWithType(),
                            IngredientList(fullShoppingList.ingredientList.elements - it)
                        ) databaseThen { ret(shoppingList) }
                    }
                }
                edit(IngredientList::class) {
                    defaultCallCC(shoppingList) {
                        getIngredients(selected = it, defaultAmountProducer = null) then {
                            databaseInteractions.saveRelatedIngredients(
                                shoppingList.asIdWithType(),
                                it
                            )
                        } databaseThen { ret(shoppingList) }
                    }
                }
            }
        }
    } then { ret(Unit) }
