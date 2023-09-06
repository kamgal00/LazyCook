package com.example.lazycook.logic.apis

import com.example.lazycook.logic.returnables.GuiAction
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.Thrower
import com.example.lazycook.logic.callCC
import com.example.lazycook.logic.returnables.Cancel
import com.example.lazycook.logic.returnables.CheckCasesContext
import com.example.lazycook.logic.returnables.CheckCasesContext.Companion.checkGuiActionCases
import com.example.lazycook.logic.returnables.DatabaseAction
import com.example.lazycook.logic.returnables.ExceptionGuiResult
import com.example.lazycook.logic.returnables.Navigate
import com.example.lazycook.logic.returnables.NavigationDestination
import com.example.lazycook.logic.returnables.SafeGuiCallResult

open class Apis(
    open val userInteractions: UserInteractions,
    open val databaseInteractions: DatabaseInteractions
)

open class ProgramContext(
    override val userInteractions: UserInteractions,
    override val databaseInteractions: DatabaseInteractions,
    val navigationScope: Thrower<NavigationDestination>
) : Apis(userInteractions, databaseInteractions) {
    fun <T> GuiAction.handleSpecialResultsAndThen(
        onError: ActionWithContinuation<T>,
        next: (SafeGuiCallResult) -> ActionWithContinuation<T>
    ): ActionWithContinuation<T> =
        this then {
            when (it) {
                is Cancel -> onError
                is ExceptionGuiResult -> userInteractions.printMessage("Error occurred: ${it.exception.message}") then {
                    onError
                }

                is Navigate -> navigationScope.exit(it.navigationPoint)
                else -> next(it as SafeGuiCallResult)
            }
        }

    fun <T, R> DatabaseAction<T>.handleDatabaseErrorsAndThen(
        onError: ActionWithContinuation<R>,
        next: (T) -> ActionWithContinuation<R>
    ): ActionWithContinuation<R> =
        this then {
            if (it.result != null) next(it.result)
            else userInteractions.printMessage("Error occurred: ${it.err?.message}") then { onError }
        }

    fun <T> GuiAction.checkCases(
        onCancel: ActionWithContinuation<T>,
        cases: CheckCasesContext<T>.() -> Unit
    ): ActionWithContinuation<T> = checkGuiActionCases(this@ProgramContext, onCancel, cases)
}

open class ExitContext(
    open val programContext: ProgramContext,
    open val onCancel: Thrower<Unit>
) : ProgramContext(
    programContext.userInteractions,
    programContext.databaseInteractions,
    programContext.navigationScope
) {

    infix fun <T> GuiAction.guiThen(
        next: (SafeGuiCallResult) -> ActionWithContinuation<T>
    ): ActionWithContinuation<T> = handleSpecialResultsAndThen(onCancel.exit(Unit), next)

    infix fun <T, R> DatabaseAction<T>.databaseThen(
        next: (T) -> ActionWithContinuation<R>
    ): ActionWithContinuation<R> = handleDatabaseErrorsAndThen(onCancel.exit(Unit), next)

    infix fun <T> GuiAction.checkCases(
        cases: CheckCasesContext<T>.() -> Unit
    ): ActionWithContinuation<T> = checkGuiActionCases(this@ExitContext, cases)
}

fun <T> ProgramContext.defaultCallCC(
    default: T,
    f: ExitContext.() -> ActionWithContinuation<T>
): ActionWithContinuation<T> =
    callCC {
        f(ExitContext(this, object : Thrower<Unit> {
            override fun <R> exit(t: Unit): ActionWithContinuation<R> = it.exit(default)
        }))
    }

fun <T> ProgramContext.whileCallCC(
    initValue: T,
    f: ExitContext.(T, Thrower<T>) -> ActionWithContinuation<T>
): ActionWithContinuation<T> =
    callCC { outerThrower ->
        fun loop(v: T): ActionWithContinuation<T> =
            f(ExitContext(this, object : Thrower<Unit> {
                override fun <R> exit(t: Unit): ActionWithContinuation<R> =
                    outerThrower.exit(v)
            }), v, outerThrower) then { loop(it) }
        loop(initValue)
    }
