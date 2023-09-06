package com.example.lazycook.room.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.lazycook.logic.dataclasses.ShoppingList

@Entity
data class RoomShoppingList(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
)

@Dao
interface ShoppingListDao : IngredientDao {

    @Query("SELECT * FROM RoomShoppingList")
    fun getAllShoppingLists(): List<ShoppingList>

    @Query("SELECT * FROM RoomShoppingList WHERE id = :id")
    fun getShoppingListWithId(id: Int): ShoppingList

    @Insert(entity = RoomShoppingList::class)
    fun insertShoppingList(shoppingList: ShoppingList): Long

    @Transaction
    fun insertShoppingListAndGet(shoppingList: ShoppingList): ShoppingList {
        val id = insertShoppingList(shoppingList)
        return getShoppingListWithId(id.toInt())
    }

    @Update(entity = RoomShoppingList::class)
    fun updateShoppingList(shoppingList: ShoppingList)

    @Transaction
    fun updateShoppingListAndGet(shoppingList: ShoppingList): ShoppingList {
        updateShoppingList(shoppingList)
        return getShoppingListWithId(shoppingList.id)
    }

    @Delete(entity = RoomShoppingList::class)
    fun deleteShoppingList(shoppingList: ShoppingList)

    @Transaction
    fun deleteShoppingListWithIngredients(shoppingList: ShoppingList) {
        deleteIngredientsOf(shoppingList.asIdWithType())
        deleteShoppingList(shoppingList)
    }

}
