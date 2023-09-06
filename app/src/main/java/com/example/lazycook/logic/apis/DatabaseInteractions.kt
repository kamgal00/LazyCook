package com.example.lazycook.logic.apis

import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.returnables.DatabaseAction
import com.example.lazycook.logic.DataObject
import com.example.lazycook.logic.dataclasses.IdWithType
import kotlin.reflect.KClass

interface DatabaseInteractions {
    fun <T : DataObject> get(cl: KClass<T>): DatabaseAction<T>
    fun <T : DataObject> getList(cl: KClass<T>): DatabaseAction<List<T>>
    fun <T : DataObject> add(obj: T): DatabaseAction<T>
    fun <T : DataObject> edit(obj: T): DatabaseAction<T>
    fun <T : DataObject> delete(obj: T): DatabaseAction<Unit>
    fun getRelatedIngredients(obj: IdWithType): DatabaseAction<IngredientList>
    fun getRelatedTags(obj: IdWithType): DatabaseAction<TagList>
    fun saveRelatedIngredients(obj: IdWithType, ing: IngredientList): DatabaseAction<IngredientList>
    fun saveRelatedTags(obj: IdWithType, tags: TagList): DatabaseAction<TagList>
    fun findAllRecipesSatisfying(name: String, tags: TagList): DatabaseAction<IngredientList>
}


