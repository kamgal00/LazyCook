package com.example.lazycook.logic

class ActionWithContinuation<out T>(private val operation: ((T) -> Unit) -> Unit) {

    fun run(continuation: (T) -> Unit = {}) = operation(continuation)

    infix fun <R> then(f: (T) -> ActionWithContinuation<R>) =
        ActionWithContinuation { rContinuation ->
            run { t -> f(t).run(rContinuation) }
        }
}

fun <T> ret(x: T) = ActionWithContinuation { continuation -> continuation(x) }

interface Thrower<in T> {
    fun <R> exit(t: T): ActionWithContinuation<R>
}

fun <T> callCC(f: (Thrower<T>) -> ActionWithContinuation<T>): ActionWithContinuation<T> =
    ActionWithContinuation { continuation ->
        f(object : Thrower<T> {
            override fun <R> exit(t: T): ActionWithContinuation<R> =
                ActionWithContinuation {
                    continuation(t)
                }
        }).run(continuation)
    }

fun <T> emptyAction(): ActionWithContinuation<T> = ActionWithContinuation {}

class EmptyThrower<T> : Thrower<T> {
    override fun <R> exit(t: T): ActionWithContinuation<R> = emptyAction()
}
