package com.example.lazycook.ui.components.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.ShoppingList
import com.example.lazycook.logic.actions.ShoppingListSelector
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.components.widgets.OverviewBox
import com.example.lazycook.ui.createOperation

@Preview(showBackground = true)
@Composable
fun ShoppingListSelectorPreview() {
    ShoppingListSelectionView(
        listSelector = ShoppingListSelector(
            listOf("Siema", "Eniu", "Twój", "Stary").map {
                ShoppingList(it.hashCode(), it)
            }
        ),
        actionConsumer = {}
    )
}

@Composable
fun ShoppingListSelectionView(
    listSelector: ShoppingListSelector,
    actionConsumer: ActionConsumer
) {
    Column(Modifier.fillMaxSize()) {
        OverviewBox(
            description = "Available lists",
            actions = emptyList()
        ) {
            listSelector.lists.forEach {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .border(1.dp, Color.Black)
                        .padding(10.dp)
                        .clickable { actionConsumer(Select(it)) },
                    contentAlignment = Center
                ) {
                    Text(text = it.name)
                }
            }
        }
    }
}
