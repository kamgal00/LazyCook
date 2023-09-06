package com.example.lazycook.ui.components.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.actions.IngredientPicker
import com.example.lazycook.logic.actions.TagSelector
import com.example.lazycook.logic.dataclasses.TextFieldReturnVal
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.components.utils.SampleRecipe
import com.example.lazycook.ui.components.utils.SampleTag
import com.example.lazycook.ui.components.widgets.IngredientListWidget
import com.example.lazycook.ui.components.widgets.TagSelectionView

@Preview(showBackground = true)
@Composable
fun IngredientSearchPreview() {
    IngredientSearchView(
        ingredientPicker = IngredientPicker(
            searchText = "XDD",
            tagSelector = TagSelector(
                TagList(listOf(SampleTag.Dinner)),
                TagList(listOf(SampleTag.Dinner, SampleTag.Breakfast, SampleTag.SecondBreakfast)),
            ),
            visibleIngredients = SampleRecipe.SampleIngredientList,
        ),
        actionConsumer = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IngredientSearchView(
    ingredientPicker: IngredientPicker,
    actionConsumer: ActionConsumer
) {
    var searchText by remember { mutableStateOf(ingredientPicker.searchText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchText,
                    onValueChange = {searchText = it},
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    actionConsumer(
                        Select(
                            TextFieldReturnVal(searchText)
                        )
                    )
                }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
            }
        }

        TagSelectionView(
            selector = ingredientPicker.tagSelector,
            actionConsumer = actionConsumer
        )

        IngredientListWidget(
            ingredients = ingredientPicker.visibleIngredients,
            actionConsumer = actionConsumer,
            description = "Found recipes",
            actions = emptyList()
        )
    }
}
