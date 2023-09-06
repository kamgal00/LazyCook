package com.example.lazycook.room.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.IdWithType
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Recipe

@Entity(
    foreignKeys = [
        ForeignKey(
            RoomRecipe::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomIngredient(
    @PrimaryKey(autoGenerate = true) val ingredientId: Int,
    val id: Int,
    val type: String,
    val unit: String,
    val amount: Double,
    val recipeId: Int
)

data class RoomIngredientData(
    @Embedded
    val ingredient: RoomIngredient,
    @Relation(
        entity = RoomRecipe::class,
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipe: Recipe
)

@Dao
interface IngredientDao {
    @Delete(entity = RoomIngredient::class)
    fun deleteIngredientsOf(obj: IdWithType)

    @Insert
    fun insertIngredients(vararg roomIngredients: RoomIngredient)
    fun addIngredientsTo(obj: IdWithType, ingredients: IngredientList) =
        insertIngredients(*ingredients.elements.filter { it.amount != null }.map {
            RoomIngredient(
                0,
                obj.id,
                obj.type,
                it.amount!!.unit,
                it.amount.amount,
                it.recipe.id
            )
        }.toTypedArray())

    @Transaction
    fun updateIngredientsOf(obj: IdWithType, ingredients: IngredientList) {
        deleteIngredientsOf(obj)
        addIngredientsTo(obj, ingredients)
    }

    @Transaction
    @Query("SELECT * FROM RoomIngredient WHERE id = :id AND type = :type")
    fun getIngredientsOf(id: Int, type: String): List<RoomIngredientData>
}