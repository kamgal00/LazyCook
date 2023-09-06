package com.example.lazycook.logic.returnables

import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.actions.showAllRecipes
import com.example.lazycook.logic.actions.showAllTags
import com.example.lazycook.logic.actions.showCalendar
import com.example.lazycook.logic.actions.showShoppingLists
import com.example.lazycook.logic.apis.ProgramContext

sealed interface NavigationDestination {
    fun showScreen(p: ProgramContext): ActionWithContinuation<Unit>
    val name: String
}

object CalendarScreen : NavigationDestination {
    override fun showScreen(p: ProgramContext): ActionWithContinuation<Unit> = p.showCalendar()
    override val name: String
        get() = "Calendar"
}

object RecipesScreen : NavigationDestination {
    override fun showScreen(p: ProgramContext): ActionWithContinuation<Unit> = p.showAllRecipes()
    override val name: String
        get() = "Recipes"
}

object ShoppingListsScreen: NavigationDestination {
    override fun showScreen(p: ProgramContext): ActionWithContinuation<Unit> = p.showShoppingLists()
    override val name: String
        get() = "Lists"
}
object TagsScreen: NavigationDestination {
    override fun showScreen(p: ProgramContext): ActionWithContinuation<Unit> = p.showAllTags()
    override val name: String
        get() = "Tags"
}
