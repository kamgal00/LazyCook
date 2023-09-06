package com.example.lazycook.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazycook.logic.dataclasses.Ingredient
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.ui.Operation
import com.example.lazycook.logic.returnables.Create
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.components.utils.AsAsyncImage
import com.example.lazycook.ui.components.utils.SampleRecipe
import com.example.lazycook.ui.components.utils.noMinIntrinsicHeight
import com.example.lazycook.ui.div
import com.example.lazycook.ui.theme.LazyCookTheme

@Preview(showBackground = true)
@Composable
fun IngredientListPreview() {
    LazyCookTheme {
        Column {
            IngredientListWidget(
                ingredients = SampleRecipe.SampleIngredientList,
                actionConsumer = {},
                description = "Ingredients"
            )
        }
    }
}

@Composable
fun IngredientListItem(
    ingredient: Ingredient,
    actionConsumer: ActionConsumer,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .background(backgroundColor)
            .clickable { actionConsumer(Select(ingredient)) }
    ) {
        Row(
            modifier = modifier
                .padding(8.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            ingredient.recipe.photo.AsAsyncImage(Modifier.size(60.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                Text(
                    text = ingredient.recipe.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.noMinIntrinsicHeight()
                )
            }
            ingredient.amount?.let {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(width = 1.dp),
                    color = Color.Black
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "Amount:")
                    Text(text = it.amount.toString() + " " + it.unit)
                }
            }
        }
    }
}

@Composable
fun IngredientListWidget(
    ingredients: IngredientList,
    actionConsumer: ActionConsumer,
    description: String,
    modifier: Modifier = Modifier,
    actions: List<Operation<ImageVector>> = emptyList(),
) {
    OverviewBox(
        description = description,
        actions = actions,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
        ) {
            items(items = ingredients.elements, key = { it.recipe.id }) {
                IngredientListItem(it, actionConsumer)
            }
        }
    }
}