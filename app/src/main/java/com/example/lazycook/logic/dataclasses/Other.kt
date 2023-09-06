package com.example.lazycook.logic.dataclasses

import com.example.lazycook.logic.DataObject
import com.example.lazycook.logic.ReturnValue
import kotlin.reflect.KClass


data class TextFieldReturnVal(val text: String) : ReturnValue

open class DataList<T : DataObject>(
    val cl: KClass<T>,
    open val elements: List<T>
) : DataObject

interface NamedData<T : DataObject> : DataObject {
    val name: String
}

data class TitleAndDescription(
    val title: String,
    val description: String?
) : DataObject
