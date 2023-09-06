package com.example.lazycook.logic.dataclasses

import com.example.lazycook.logic.DataObject
import com.example.lazycook.logic.ReturnValue

data class Recipe(
    val id: Int,
    val photo: String?,
    val name: String,
    val description: String?,
    val measures: AmountList,
) : DataObject {
    fun asIdWithType() = IdWithType(id, "recipe")
}

data class Amount(val unit: String, val amount: Double) : DataObject

data class AmountList(val listElements: List<Amount>): DataObject
     {
    fun asMap(): Map<String, Double> =
        listElements.groupBy({ it.unit }, { it.amount }).mapValues { it.value.first() } + Pair(
            "unit",
            1.0
        )

    fun normalize(): AmountList = asMap().asAmountList()

    companion object {
        fun Map<String, Double>.asAmountList() =
            AmountList((this - "unit").toList().map { Amount(it.first, it.second) })
    }
}

data class Ingredient(val recipe: Recipe, val amount: Amount?) : DataObject

data class IngredientList(override val elements: List<Ingredient>) :
    DataList<Ingredient>(Ingredient::class, elements) {
    infix fun withAmountsFrom(other: IngredientList) =
        other.elements.associateBy { it.recipe.id }.let { ingMap ->
            IngredientList(elements.map { it.copy(amount = ingMap[it.recipe.id]?.amount) })
        }
}


