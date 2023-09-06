package com.example.lazycook.ui.components.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazycook.logic.actions.FullInfoRecipe
import com.example.lazycook.ui.Operation
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.dataclasses.TitleAndDescription
import com.example.lazycook.logic.actions.TagSelector
import com.example.lazycook.logic.returnables.Edit
import com.example.lazycook.logic.returnables.PhotoGallery
import com.example.lazycook.logic.returnables.PhotoTake
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.components.utils.AsAsyncImage
import com.example.lazycook.ui.components.utils.AsIconButton
import com.example.lazycook.ui.components.utils.SampleRecipe
import com.example.lazycook.ui.components.utils.SampleTag
import com.example.lazycook.ui.components.utils.noMinIntrinsicHeight
import com.example.lazycook.ui.components.widgets.IngredientListWidget
import com.example.lazycook.ui.components.widgets.ShowMeasures
import com.example.lazycook.ui.components.widgets.TagSelectionView
import com.example.lazycook.ui.createOperation
import com.example.lazycook.ui.div
import com.example.lazycook.ui.editOperation

@Preview(showBackground = true)
@Composable
fun RecipeViewPreview() {
    RecipeView(
        fullInfoRecipe = FullInfoRecipe(
            SampleRecipe.cat,
            SampleRecipe.SampleIngredientList,
            SampleTag.SampleTagList
        ),
        actionConsumer = {}
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeView(
    fullInfoRecipe: FullInfoRecipe,
    actionConsumer: ActionConsumer,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Min)
            ) {

                fullInfoRecipe.recipe.photo.AsAsyncImage(Modifier.size(130.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Operation(
                        Icons.Default.PhotoAlbum,
                        actionConsumer / Select(PhotoGallery)
                    ).AsIconButton(
                        Modifier.weight(1f)
                    )
                    Operation(
                        Icons.Default.PhotoCamera,
                        actionConsumer / Select(PhotoTake)
                    ).AsIconButton(
                        Modifier.weight(1f)
                    )
                }
            }
            val descriptionScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .verticalScroll(descriptionScrollState)
                    .noMinIntrinsicHeight()
                    .clickable {
                        actionConsumer(
                            Edit(
                                TitleAndDescription(
                                    fullInfoRecipe.recipe.name,
                                    fullInfoRecipe.recipe.description
                                )
                            )
                        )
                    }
            ) {
                Text(
                    text = fullInfoRecipe.recipe.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 25.sp,
                )
                Text(
                    text = fullInfoRecipe.recipe.description.orEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
        ShowMeasures(
            list = fullInfoRecipe.recipe.measures,
            actionConsumer = actionConsumer,
            actions = listOf(createOperation(fullInfoRecipe.recipe.measures, actionConsumer))
        )
        TagSelectionView(
            selector = TagSelector(TagList(emptyList()), fullInfoRecipe.tagList),
            actionConsumer = {},
            actions = listOf(editOperation(fullInfoRecipe.tagList, actionConsumer))
        )
        IngredientListWidget(
            description = "Ingredients",
            ingredients = fullInfoRecipe.ingredientList,
            actionConsumer = actionConsumer,
            modifier = Modifier.height((LocalConfiguration.current.screenHeightDp * 4 / 5).dp),
            actions = listOf(editOperation(fullInfoRecipe.ingredientList, actionConsumer))
        )

    }
}