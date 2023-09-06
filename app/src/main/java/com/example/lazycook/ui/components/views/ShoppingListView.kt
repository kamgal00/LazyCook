package com.example.lazycook.ui.components.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lazycook.logic.dataclasses.ShoppingList
import com.example.lazycook.ui.ActionConsumer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.tooling.preview.Preview
import com.example.lazycook.logic.actions.FullInfoShoppingList
import com.example.lazycook.logic.dataclasses.TextFieldReturnVal
import com.example.lazycook.logic.returnables.Delete
import com.example.lazycook.logic.returnables.Edit
import com.example.lazycook.ui.components.utils.SampleRecipe
import com.example.lazycook.ui.components.widgets.IngredientListWidget
import com.example.lazycook.ui.editOperation

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview() {
    ShoppingListView(
        fullInfoShoppingList = FullInfoShoppingList(
            ShoppingList(0, "Rzeczy które ukradł twój stary"),
            SampleRecipe.SampleIngredientList
        ),
        actionConsumer = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListView(
    fullInfoShoppingList: FullInfoShoppingList,
    actionConsumer: ActionConsumer
) {
    var title by remember { mutableStateOf(fullInfoShoppingList.shoppingList.name) }
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = CenterVertically) {
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 5.dp)
            )
            IconButton(onClick = { actionConsumer(Edit(TextFieldReturnVal(title))) }) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
            }
            IconButton(onClick = { actionConsumer(Delete(fullInfoShoppingList.shoppingList)) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
        IngredientListWidget(
            ingredients = fullInfoShoppingList.ingredientList,
            actionConsumer = actionConsumer,
            description = "Elements",
            actions = listOf(editOperation(fullInfoShoppingList.ingredientList, actionConsumer))
        )
    }
}

