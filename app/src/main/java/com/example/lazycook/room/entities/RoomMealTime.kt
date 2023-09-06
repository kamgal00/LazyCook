package com.example.lazycook.room.entities

import androidx.room.ColumnInfo
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
import androidx.room.Update
import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.Tag

@Entity(
    foreignKeys = [
        ForeignKey(
            RoomTag::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("relatedTagId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomMealTime(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val time: Int,
    val calories: Int?,
    @ColumnInfo(defaultValue = "NULL")
    val relatedTagId: Int?
)

data class RoomMealTimeWithTag(
    @Embedded
    val mealTime: RoomMealTime,
    @Relation(
        parentColumn = "relatedTagId",
        entityColumn = "id"
    )
    val tag: RoomTag?
) {
    fun asMealTime() =
        MealTime(
            mealTime.id,
            mealTime.time,
            mealTime.calories,
            tag?.let { Tag(it.id, it.name) })
}

@Dao
interface MealTimeDao {
    @Transaction
    @Query("SELECT * FROM RoomMealTime")
    fun getAllRoomMealTimesAndTags(): List<RoomMealTimeWithTag>

    fun getAllMealTimes(): List<MealTime> = getAllRoomMealTimesAndTags().map { it.asMealTime() }

    @Insert(entity = RoomMealTime::class)
    fun insertMealTime(mealTime: MealTime): Long

    @Transaction
    @Query("SELECT * FROM RoomMealTime WHERE id = :id")
    fun getRoomMealTimeWithId(id: Int): RoomMealTimeWithTag

    @Transaction
    fun insertMealTimeAndGet(mealTime: MealTime): MealTime {
        val id = insertMealTime(mealTime).toInt()
        return getRoomMealTimeWithId(id).asMealTime()
    }

    @Update
    fun updateMealTime(mealTime: RoomMealTime)

    @Transaction
    fun updateMealTimeAndGet(mealTime: MealTime): MealTime {
        updateMealTime(
            RoomMealTime(mealTime.id, mealTime.time, mealTime.calories, mealTime.relatedTag?.id)
        )
        return getRoomMealTimeWithId(mealTime.id).asMealTime()
    }

//    @Delete(entity = RoomMealTime::class)
    @Query("DELETE FROM RoomMealTime WHERE id = :id")
    fun deleteMealTimeById(id: Int)
}
