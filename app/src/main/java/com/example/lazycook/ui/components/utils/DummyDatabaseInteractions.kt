package com.example.lazycook.ui.components.utils

import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.ShoppingList
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.DataObject
import com.example.lazycook.logic.apis.DatabaseInteractions
import com.example.lazycook.logic.dataclasses.IdWithType
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.DataBaseCallResult
import com.example.lazycook.logic.returnables.DatabaseAction
import kotlin.reflect.KClass

object DummyDatabaseInteractions : DatabaseInteractions {
    override fun <T : DataObject> get(cl: KClass<T>): DatabaseAction<T> = ret(
        when (cl) {
            IngredientList::class -> DataBaseCallResult(
                SampleRecipe.SampleIngredientList.let {
                    IngredientList(it.elements.map {
                        Ingredient(
                            it.recipe,
                            null
                        )
                    })
                } as T
            )

            else -> DataBaseCallResult(null, Error("No get for ${cl.simpleName}"))
        }
    )


    override fun <T : DataObject> getList(cl: KClass<T>): DatabaseAction<List<T>> = ret(
        when (cl) {
            MealTime::class -> DataBaseCallResult(
                listOf(
                    SampleMealTime.Breakfast,
                    SampleMealTime.SecondBreakfast,
                    SampleMealTime.Dinner,
                    SampleMealTime.Lunch
                ) as List<T>
            )

            Meal::class -> DataBaseCallResult(
                listOf(
                    SampleMeal.SampleBreakfast,
                    SampleMeal.SampleDinner,
                    SampleMeal.SampleLunch,
                ) as List<T>
            )

            Tag::class -> DataBaseCallResult(
                listOf(
                    SampleTag.Breakfast,
                    SampleTag.SecondBreakfast,
                    SampleTag.Dinner,
                    SampleTag.Lunch,
                    SampleTag.Vegetarian
                ) as List<T>
            )

            ShoppingList::class -> DataBaseCallResult(
                listOf("Siema", "Eniu", "Twój", "Stary").map {
                    ShoppingList(it.hashCode(), it)
                } as List<T>
            )

            else ->
                DataBaseCallResult(null, Error("No getList for ${cl.simpleName}"))
        }
    )

    override fun <T : DataObject> add(obj: T): DatabaseAction<T> = ret(DataBaseCallResult(obj))


    override fun <T : DataObject> edit(obj: T): DatabaseAction<T> = ret(DataBaseCallResult(obj))

    override fun <T : DataObject> delete(obj: T): DatabaseAction<Unit> =
        ret(DataBaseCallResult(Unit))

    override fun getRelatedIngredients(obj: IdWithType): DatabaseAction<IngredientList> =
        ret(DataBaseCallResult(SampleRecipe.SampleIngredientList))

    override fun getRelatedIngredientsSync(obj: IdWithType): IngredientList {
        TODO("Not yet implemented")
    }

    override fun getRelatedTags(obj: IdWithType): DatabaseAction<TagList> =
        ret(DataBaseCallResult(SampleTag.SampleTagList))

    override fun saveRelatedIngredients(
        obj: IdWithType,
        ing: IngredientList
    ): DatabaseAction<IngredientList> =
        ret(DataBaseCallResult(SampleRecipe.SampleIngredientList))

    override fun saveRelatedTags(obj: IdWithType, tags: TagList): DatabaseAction<TagList> =
        ret(DataBaseCallResult(SampleTag.SampleTagList))


    override fun findAllRecipesSatisfying(
        name: String,
        tags: TagList
    ): DatabaseAction<IngredientList> = get(IngredientList::class) then {
        if (it.result != null) {
            ret(
                DataBaseCallResult(
                    IngredientList(
                        it.result.elements.filter {
                            it.recipe.name.contains(
                                name,
                                ignoreCase = true
                            ) // no Tags filtering!!1
                        }
                    ),
                    it.err
                )
            )
        } else ret(it)
    }
}