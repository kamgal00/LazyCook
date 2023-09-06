package com.example.lazycook.logic.dataclasses

import com.example.lazycook.logic.DataObject


data class ShoppingList(
    val id: Int,
    val name: String,
) : DataObject {
    fun asIdWithType() = IdWithType(id, "shoppingList")
}