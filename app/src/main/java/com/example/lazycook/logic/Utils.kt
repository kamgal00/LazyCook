package com.example.lazycook.logic


fun <R, T> ActionWithContinuation<T?>.ifNotNullThen(
    onNull: ActionWithContinuation<R>,
    f: (T) -> ActionWithContinuation<R>
) =
    then {
        if (it == null) onNull else f(it)
    }


