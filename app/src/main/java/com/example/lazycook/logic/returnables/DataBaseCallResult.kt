package com.example.lazycook.logic.returnables

import com.example.lazycook.logic.ActionWithContinuation

data class DataBaseCallResult<T>(val result: T?, val err: Error? = null)

typealias DatabaseAction<T> = ActionWithContinuation<DataBaseCallResult<T>>
