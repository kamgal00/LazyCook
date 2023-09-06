package com.example.lazycook.room

import androidx.annotation.NonNull
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.lazycook.logic.dataclasses.IdWithType
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.room.entities.IngredientDao
import com.example.lazycook.room.entities.RoomRecipe
import com.example.lazycook.room.entities.TagDao
import kotlin.reflect.KClass

//@Dao
//abstract class RecipeDao<T : IdWithType>(
//    private val targetEntityClass: KClass<out Any>
//) : IngredientDao, TagDao {
//    @Insert(entity = targetEntityClass)
//    abstract fun insert(obj: T)
//
//    @Delete(entity = RoomRecipe::class)
//    fun _deleteRecipe(recipe: Recipe)
//
//    @Update(entity = RoomRecipe::class)
//    fun updateRecipe(recipe: Recipe)
//
//    @Query("SELECT * FROM RoomRecipe")
//    fun getAllRecipes(): List<Recipe>
//
//    @Transaction
//    fun deleteRecipe(recipe: Recipe) {
//        _deleteRecipe(recipe)
//        deleteIngredientsOf(recipe)
//        deleteTagsOf(recipe)
//    }
//}
