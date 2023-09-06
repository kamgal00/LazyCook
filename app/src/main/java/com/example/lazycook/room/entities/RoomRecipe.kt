package com.example.lazycook.room.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.Recipe


@Entity()
data class RoomRecipe(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val photo: String,
    val name: String,
    val description: String?,
    val measures: AmountList,
)

@Dao
interface RecipeDao : IngredientDao, TagDao {
    @Insert(entity = RoomRecipe::class)
    fun insertRecipe(recipe: Recipe): Long

    @Update(entity = RoomRecipe::class)
    fun updateRecipe(recipe: Recipe)

    @Query("SELECT * FROM RoomRecipe")
    fun getAllRecipes(): List<Recipe>

    @Query("SELECT * FROM RoomRecipe WHERE id = :id")
    fun getRecipeWithId(id: Int): Recipe

    @Transaction
    fun insertRecipeAndGet(recipe: Recipe): Recipe {
        val id = insertRecipe(recipe)
        return getRecipeWithId(id.toInt())
    }

    @Transaction
    fun updateRecipeAndGet(recipe: Recipe): Recipe {
        updateRecipe(recipe)
        return getRecipeWithId(recipe.id)
    }

//    @Delete(entity = RoomRecipe::class)
    @Query("DELETE FROM RoomRecipe WHERE id = :id")
    fun deleteRecipeById(id: Int)

    @Transaction
    fun deleteRecipeWithTagsAndIngredients(recipe: Recipe) {
        deleteIngredientsOf(recipe.asIdWithType())
        deleteTagsOf(recipe.asIdWithType())
        deleteRecipeById(recipe.id)
    }
}