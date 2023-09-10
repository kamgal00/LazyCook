package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.emptyAction
import com.example.lazycook.logic.ret

data class AmountEditor(
    val amount: Amount?
) : GuiElement


fun ExitContext.getNewAmount(): ActionWithContinuation<Amount> =
    userInteractions.show(AmountEditor(null)) checkCases {
        create(Amount::class) {
            ret(it)
        }
    }

fun ExitContext.editOrDeleteAmount(amount: Amount): ActionWithContinuation<Amount?> =
    userInteractions.show(AmountEditor(amount)) checkCases {
        edit(Amount::class) {
            ret(it)
        }
        delete(Amount::class) {
            ret(null)
        }
    }

data class AmountSelector(
    val possibleMeasures: Map<String, Double>,
    val previousAmount: Amount?
) : GuiElement


fun ExitContext.chooseAmount(
    possibleAmounts: AmountList,
    previousAmount: Amount?
): ActionWithContinuation<Amount?> =
    userInteractions.show(
        AmountSelector(
            possibleAmounts.asMap(),
            previousAmount
        )
    ) checkCases {
        select(Amount::class) {
            ret(it)
        }
        delete {
            ret(null)
        }
    }
