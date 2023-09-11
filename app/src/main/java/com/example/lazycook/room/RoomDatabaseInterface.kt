package com.example.lazycook.room

import android.content.Context
import android.icu.util.Measure
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lazycook.logic.DataObject
import com.example.lazycook.logic.apis.DatabaseInteractions
import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.logic.dataclasses.IdWithType
import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.logic.dataclasses.ShoppingList
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.DataBaseCallResult
import com.example.lazycook.logic.returnables.DatabaseAction
import com.example.lazycook.logic.returnables.RecipesScreen
import com.example.lazycook.room.entities.IngredientDao
import com.example.lazycook.room.entities.MealDao
import com.example.lazycook.room.entities.MealTimeDao
import com.example.lazycook.room.entities.RecipeDao
import com.example.lazycook.room.entities.RoomIngredient
import com.example.lazycook.room.entities.RoomMeal
import com.example.lazycook.room.entities.RoomMealTime
import com.example.lazycook.room.entities.RoomRecipe
import com.example.lazycook.room.entities.RoomShoppingList
import com.example.lazycook.room.entities.RoomTag
import com.example.lazycook.room.entities.RoomTagElement
import com.example.lazycook.room.entities.ShoppingListDao
import com.example.lazycook.room.entities.TagDao
import kotlin.reflect.KClass

@Database(
    entities = [
        RoomRecipe::class,
        RoomMealTime::class,
        RoomMeal::class,
        RoomTag::class,
        RoomShoppingList::class,
        RoomIngredient::class,
        RoomTagElement::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipesDao(): RecipeDao
    abstract fun mealTimeDao(): MealTimeDao
    abstract fun mealDao(): MealDao
    abstract fun tagDao(): TagDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun ingredientDao(): IngredientDao
}

class RoomDatabaseInterface(appContext: Context) : DatabaseInteractions {
    private val db = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "main-database"
    ).allowMainThreadQueries().build()

    override fun <T : DataObject> get(cl: KClass<T>): DatabaseAction<T> = ret(
        when (cl) {
            IngredientList::class -> DataBaseCallResult(
                IngredientList(db.recipesDao().getAllRecipes().map { Ingredient(it, null) }) as T
            )

            else -> DataBaseCallResult(null, Error("No get for ${cl.simpleName}"))
        }
    )

    override fun <T : DataObject> getList(cl: KClass<T>): DatabaseAction<List<T>> = ret(
        when (cl) {
            MealTime::class -> DataBaseCallResult(
                db.mealTimeDao().getAllMealTimes() as List<T>
            )

            Meal::class -> DataBaseCallResult(
                db.mealDao().getAllMeals() as List<T>
            )

            Tag::class -> DataBaseCallResult(
                db.tagDao().getAllTags() as List<T>
            )

            ShoppingList::class -> DataBaseCallResult(
                db.shoppingListDao().getAllShoppingLists() as List<T>
            )

            else ->
                DataBaseCallResult(null, Error("No getList for ${cl.simpleName}"))
        }
    )

    override fun <T : DataObject> add(obj: T): DatabaseAction<T> = ret(
        when (obj) {
            is MealTime -> DataBaseCallResult(db.mealTimeDao().insertMealTimeAndGet(obj) as T)
            is Meal -> DataBaseCallResult(db.mealDao().insertMealAndGet(obj) as T)
            is Recipe -> DataBaseCallResult(db.recipesDao().insertRecipeAndGet(obj) as T)
            is ShoppingList -> DataBaseCallResult(
                db.shoppingListDao().insertShoppingListAndGet(obj) as T
            )

            is Tag -> DataBaseCallResult(db.tagDao().insertTagAndGet(obj) as T)
            else ->
                DataBaseCallResult(null, Error("Error occurred while inserting ${obj::class}"))
        }
    )


    override fun <T : DataObject> edit(obj: T): DatabaseAction<T> = ret(
        when (obj) {
            is MealTime -> DataBaseCallResult(db.mealTimeDao().updateMealTimeAndGet(obj) as T)
            is Recipe -> DataBaseCallResult(db.recipesDao().updateRecipeAndGet(obj) as T)
            is ShoppingList -> DataBaseCallResult(
                db.shoppingListDao().updateShoppingListAndGet(obj) as T
            )

            else ->
                DataBaseCallResult(null, Error("Error occurred while editing ${obj::class}"))
        }
    )

    override fun <T : DataObject> delete(obj: T): DatabaseAction<Unit> = ret(
        when (obj) {
            is MealTime -> DataBaseCallResult(db.mealTimeDao().deleteMealTimeById(obj.id))
            is Meal -> DataBaseCallResult(db.mealDao().deleteMealWithIngredients(obj))
            is Recipe -> DataBaseCallResult(db.recipesDao().deleteRecipeWithTagsAndIngredients(obj))
            is ShoppingList -> DataBaseCallResult(
                db.shoppingListDao().deleteShoppingListWithIngredients(obj)
            )

            is TagList -> DataBaseCallResult(
                db.tagDao().deleteTags(*obj.elements.toTypedArray())
            )

            else ->
                DataBaseCallResult(null, Error("Error occurred while deleting ${obj::class}"))
        }
    )

    override fun getRelatedIngredients(obj: IdWithType): DatabaseAction<IngredientList> =
        ret(DataBaseCallResult(getRelatedIngredientsSync(obj)))

    override fun getRelatedIngredientsSync(obj: IdWithType): IngredientList =
        db.ingredientDao().getIngredientsOf(obj.id, obj.type)
            .map { Ingredient(it.recipe, Amount(it.ingredient.unit, it.ingredient.amount)) }
            .let { IngredientList(it) }

    override fun getRelatedTags(obj: IdWithType): DatabaseAction<TagList> =
        ret(
            DataBaseCallResult(
                db.tagDao().getTagsOf(obj.id, obj.type)
                    .map { it.tag }.let { TagList(it) })
        )

    private fun syncGetRelatedTags(obj: IdWithType): TagList =
        db.tagDao().getTagsOf(obj.id, obj.type)
            .map { it.tag }.let { TagList(it) }

    override fun saveRelatedIngredients(
        obj: IdWithType,
        ing: IngredientList
    ): DatabaseAction<IngredientList> {
        db.ingredientDao().updateIngredientsOf(obj, ing)
        return ret(DataBaseCallResult(ing))
    }

    override fun saveRelatedTags(obj: IdWithType, tags: TagList): DatabaseAction<TagList> {
        db.tagDao().updateTagsOf(obj, tags)
        return ret(DataBaseCallResult(tags))
    }

    override fun findAllRecipesSatisfying(
        name: String,
        tags: TagList
    ): DatabaseAction<IngredientList> = ret(
        DataBaseCallResult(
            //TODO: optimize
            db.recipesDao().getAllRecipes()
                .map { Pair(it, syncGetRelatedTags(it.asIdWithType())) }
                .filter {
                    it.first.name.contains(name, ignoreCase = true) && it.second.elements.containsAll(tags.elements)
                }
                .map { Ingredient(it.first, null) }
                .let { IngredientList(it) })
    )

}

