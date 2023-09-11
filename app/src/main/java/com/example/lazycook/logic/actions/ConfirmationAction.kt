package com.example.lazycook.logic.actions

import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.ReturnValue
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.ret

data class ConfirmationElement(
    val message: String
) : GuiElement

data class ConfirmationResult(
    val result: Boolean
) : ReturnValue


fun ExitContext.confirm(message: String): ActionWithContinuation<Boolean> =
    userInteractions.show(ConfirmationElement(message)) checkCases {
        select(ConfirmationResult::class) {
            ret(it.result)
        }
    }