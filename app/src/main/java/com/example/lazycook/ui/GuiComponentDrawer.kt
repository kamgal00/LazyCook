package com.example.lazycook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.Recipe
import com.example.lazycook.logic.dataclasses.ShoppingList
import com.example.lazycook.logic.dataclasses.TitleAndDescription
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.actions.AllTagsElement
import com.example.lazycook.logic.actions.AmountEditor
import com.example.lazycook.logic.actions.AmountSelector
import com.example.lazycook.logic.actions.CalendarElement
import com.example.lazycook.logic.actions.DateRangeSelector
import com.example.lazycook.logic.actions.FullInfoMeal
import com.example.lazycook.logic.actions.FullInfoRecipe
import com.example.lazycook.logic.actions.FullInfoShoppingList
import com.example.lazycook.logic.actions.IngredientPicker
import com.example.lazycook.logic.actions.ShoppingListSelector
import com.example.lazycook.logic.actions.TagSelector
import com.example.lazycook.ui.components.views.AllTagsView
import com.example.lazycook.ui.components.views.IngredientSearchView
import com.example.lazycook.ui.components.views.MealCalendar
import com.example.lazycook.ui.components.views.MealTimeView
import com.example.lazycook.ui.components.views.MealView
import com.example.lazycook.ui.components.views.RecipeView
import com.example.lazycook.ui.components.views.ShoppingListSelectionView
import com.example.lazycook.ui.components.views.ShoppingListView
import com.example.lazycook.ui.components.widgets.ChooseAmountView
import com.example.lazycook.ui.components.widgets.EnterUnitAndAmount
import com.example.lazycook.ui.components.widgets.IngredientListWidget
import com.example.lazycook.ui.components.widgets.ShowMeasures
import com.example.lazycook.ui.components.widgets.TagSelectionView
import com.example.lazycook.ui.components.widgets.TitleAndDescriptionEditor

@Composable
fun DrawGuiElement(
    element: GuiElement?,
    onAction: ActionConsumer,
    modifier: Modifier = Modifier
) {
    when (element) {
        is AllTagsElement -> AllTagsView(element, onAction)
        is TagSelector -> TagSelectionView(selector = element, actionConsumer = onAction)
        is ShoppingListSelector -> ShoppingListSelectionView(
            listSelector = element,
            actionConsumer = onAction
        )

        is AmountSelector -> ChooseAmountView(selector = element, actionConsumer = onAction)
        is AmountEditor -> EnterUnitAndAmount(amountEditor = element, actionConsumer = onAction)
        is IngredientPicker -> IngredientSearchView(
            ingredientPicker = element,
            actionConsumer = onAction
        )

        is FullInfoShoppingList -> ShoppingListView(
            fullInfoShoppingList = element,
            actionConsumer = onAction
        )

        is AmountList -> ShowMeasures(list = element, actionConsumer = onAction)
        is TitleAndDescription -> TitleAndDescriptionEditor(
            titleAndDescription = element,
            actionConsumer = onAction
        )

        is CalendarElement -> MealCalendar(element = element, actionConsumer = onAction)
        is DateRangeSelector -> MealCalendar(
            element = CalendarElement(element.mealTimes, emptyList(), element.currentDate),
            actionConsumer = onAction,
            selectedSlot = element.selectedSlot
        )

        is MealTime -> MealTimeView(mealTime = element, actionConsumer = onAction)
        is FullInfoMeal -> MealView(fullMeal = element, actionConsumer = onAction)
        is IngredientList -> IngredientListWidget(
            ingredients = element,
            actionConsumer = onAction,
            description = "Ingredients"
        )

        is FullInfoRecipe -> RecipeView(fullInfoRecipe = element, actionConsumer = onAction)
        else -> Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Nothing here...", modifier = Modifier.align(Alignment.Center))
        }
    }
}