package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.ShoppingList
import com.example.lazycook.logic.dataclasses.TextFieldReturnVal
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.Create
import com.example.lazycook.logic.returnables.Delete


data class ShoppingListSelector(
    val lists: List<ShoppingList>,
) : GuiElement

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
                        databaseInteractions.delete(shoppingList) databaseThen {
                            loopScope.exit(shoppingList)
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
