package com.example.lazycook.logic.returnables

import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.ReturnValue
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.emptyAction
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

sealed interface SafeGuiCallResult : GuiCallResult
sealed interface GuiCallResult
data class Select(val selected: ReturnValue) : SafeGuiCallResult
data class Edit(val selected: ReturnValue) : SafeGuiCallResult
data class Create(val selected: ReturnValue) : SafeGuiCallResult
data class Delete(val selected: ReturnValue) : SafeGuiCallResult
data class Navigate(val navigationPoint: NavigationDestination) : GuiCallResult
object Done : SafeGuiCallResult

object Cancel : GuiCallResult
data class ExceptionGuiResult(val exception: Exception) : GuiCallResult

typealias GuiAction = ActionWithContinuation<GuiCallResult>

class CheckCasesContext<T> private constructor(
    private val result: SafeGuiCallResult,
    private var finalAction: ActionWithContinuation<T>? = null
) {

    private fun set(code: ActionWithContinuation<T>) {
        if (finalAction == null) finalAction = code
    }

    fun select(code: (Select) -> ActionWithContinuation<T>) {
        (result as? Select)?.let { set(code(it)) }
    }

    fun <R : ReturnValue> select(cl: KClass<R>, code: (R) -> ActionWithContinuation<T>) {
        (result as? Select)?.let { cl.safeCast(it.selected) }?.let { set(code(it)) }
    }

    fun select(r: ReturnValue, code: (Select) -> ActionWithContinuation<T>) {
        if (result is Select && result.selected == r) set(code(result))
    }

    fun edit(code: (Edit) -> ActionWithContinuation<T>) {
        (result as? Edit)?.let { set(code(it)) }
    }

    fun <R : ReturnValue> edit(cl: KClass<R>, code: (R) -> ActionWithContinuation<T>) {
        (result as? Edit)?.let { cl.safeCast(it.selected) }?.let { set(code(it)) }
    }

    fun edit(r: ReturnValue, code: (Edit) -> ActionWithContinuation<T>) {
        if (result is Edit && result.selected == r) set(code(result))
    }

    fun create(code: (Create) -> ActionWithContinuation<T>) {
        (result as? Create)?.let { set(code(it)) }
    }

    fun <R : ReturnValue> create(cl: KClass<R>, code: (R) -> ActionWithContinuation<T>) {
        (result as? Create)?.let { cl.safeCast(it.selected) }?.let { set(code(it)) }
    }

    fun create(r: ReturnValue, code: (Create) -> ActionWithContinuation<T>) {
        if (result is Create && result.selected == r) set(code(result))
    }

    fun delete(code: (Delete) -> ActionWithContinuation<T>) {
        (result as? Delete)?.let { set(code(it)) }
    }

    fun <R : ReturnValue> delete(cl: KClass<R>, code: (R) -> ActionWithContinuation<T>) {
        (result as? Delete)?.let { cl.safeCast(it.selected) }?.let { set(code(it)) }
    }

    fun delete(r: ReturnValue, code: (Delete) -> ActionWithContinuation<T>) {
        if (result is Delete && result.selected == r) set(code(result))
    }

    fun done(code: (Done) -> ActionWithContinuation<T>) {
        (result as? Done)?.let { set(code(it)) }
    }

    companion object {
        fun <T> GuiAction.checkGuiActionCases(
            programContext: ProgramContext,
            onCancel: ActionWithContinuation<T>,
            cases: CheckCasesContext<T>.() -> Unit
        ): ActionWithContinuation<T> {
            return with(programContext) {
                this@checkGuiActionCases.handleSpecialResultsAndThen(onCancel) {
                    val context = CheckCasesContext<T>(it)
                    cases(context)
                    context.finalAction ?: emptyAction()
                }
            }
        }

        fun <T> GuiAction.checkGuiActionCases(
            apis: ExitContext,
            cases: CheckCasesContext<T>.() -> Unit
        ): ActionWithContinuation<T> = checkGuiActionCases(apis, apis.onCancel.exit(Unit), cases)
    }
}


