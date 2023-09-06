package com.example.lazycook.room.entities

import android.util.Log
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
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.MealDate
import com.example.lazycook.logic.dataclasses.MealTime


@Entity(
    foreignKeys = [
        ForeignKey(
            RoomMealTime::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("mealTimeId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomMeal(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val startDate: MealDate,
    val endDate: MealDate,
    val mealTimeId: Int,
)

data class RoomMealWithMealTime(
    @Embedded
    val meal: RoomMeal,
    @Relation(
        entity = RoomMealTime::class,
        parentColumn = "mealTimeId",
        entityColumn = "id"
    )
    val mealTime: RoomMealTimeWithTag
) {
    fun asMeal() =
        Meal(meal.id, meal.startDate, meal.endDate, mealTime.asMealTime())
}

@Dao
interface MealDao : IngredientDao {
    @Transaction
    @Query("SELECT * FROM RoomMeal")
    fun getAllRoomMealsWithMealTime(): List<RoomMealWithMealTime>

    fun getAllMeals(): List<Meal> = getAllRoomMealsWithMealTime().map {
        it.asMeal()
    }

    @Query("SELECT * FROM RoomMeal WHERE id = :id")
    fun getMealWithId(id: Int): RoomMealWithMealTime

    @Insert
    fun insertRoomMeal(meal: RoomMeal): Long

    @Transaction
    fun insertMealAndGet(meal: Meal): Meal {
        val id = insertRoomMeal(RoomMeal(meal.id, meal.startDate, meal.endDate, meal.mealTime.id))
        return getMealWithId(id.toInt()).asMeal()
    }

    @Delete(entity = RoomMeal::class)
    fun deleteMeal(meal: Meal)

    @Transaction
    fun deleteMealWithIngredients(meal: Meal) {
        deleteIngredientsOf(meal.asIdWithType())
        deleteMeal(meal)
    }

}