package com.example.lazycook.logic.apis

import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.returnables.GuiAction
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.returnables.NavigationDestination
import com.example.lazycook.logic.returnables.SafeGuiCallResult

interface UserInteractions {
    fun askForConfirmation(msg: String): GuiAction

    fun show(
        guiElement: GuiElement,
        additionalDescription: String? = null,
        additionalOperations: List<Pair<String, SafeGuiCallResult>> = emptyList()
    ): GuiAction

    fun notifyCurrentScreen(screen: NavigationDestination): ActionWithContinuation<Unit>
    fun printMessage(msg: String): ActionWithContinuation<Unit>
}